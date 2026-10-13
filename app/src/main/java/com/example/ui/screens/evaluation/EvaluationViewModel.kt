package com.example.ui.screens.evaluation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.model.Answer
import com.example.domain.model.EvaluationResult
import com.example.domain.model.StudentSubmission
import com.example.domain.model.SubmissionType
import com.example.domain.model.User
import com.example.domain.repository.EvaluationRepository
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.StudentSubmissionRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class EvaluationViewModel(
    private val userRepository: UserRepository,
    private val evaluationRepository: EvaluationRepository,
    private val learningRepository: LearningRepository? = null,
    private val studentSubmissionRepository: StudentSubmissionRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluationState())
    val uiState: StateFlow<EvaluationState> = _uiState.asStateFlow()

    private var activeUserId: Int = -1
    private var activeUser: User? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val pgList = getSamplePgQuestions()
            val essayList = getSampleEssayQuestions()

            userRepository.getAllStudents()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat pengguna")
                    }
                }
                .collect { users ->
                    val user = users.lastOrNull()
                    if (user != null) {
                        activeUser = user
                        activeUserId = user.id
                        loadSavedAnswers(user.id, pgList, essayList)
                        checkExistingResult(user.id, pgList, essayList)
                    } else {
                        activeUserId = 1
                        loadSavedAnswers(1, pgList, essayList)
                        checkExistingResult(1, pgList, essayList)
                    }
                }
        }
    }

    private fun loadSavedAnswers(
        userId: Int,
        pgList: List<PgQuestion>,
        essayList: List<EssayQuestion>
    ) {
        val repo = learningRepository ?: return
        viewModelScope.launch {
            val savedPgMap = mutableMapOf<Int, Int>()
            pgList.forEach { q ->
                val ans = repo.getAnswerByQuestion(userId, "EVAL_PG_${q.id}")?.answerText?.toIntOrNull()
                if (ans != null) {
                    savedPgMap[q.id] = ans
                }
            }
            val savedEssayMap = mutableMapOf<Int, String>()
            essayList.forEach { q ->
                val ans = repo.getAnswerByQuestion(userId, "EVAL_ESSAY_${q.id}")?.answerText
                if (!ans.isNullOrBlank()) {
                    savedEssayMap[q.id] = ans
                }
            }
            _uiState.update { state ->
                state.copy(
                    selectedPgAnswers = if (savedPgMap.isNotEmpty()) savedPgMap else state.selectedPgAnswers,
                    essayAnswers = if (savedEssayMap.isNotEmpty()) savedEssayMap else state.essayAnswers
                )
            }
        }
    }

    private fun checkExistingResult(
        userId: Int,
        pgList: List<PgQuestion>,
        essayList: List<EssayQuestion>
    ) {
        viewModelScope.launch {
            evaluationRepository.getResultByUser(userId)
                .catch {
                    _uiState.update {
                        it.copy(pgQuestions = pgList, essayQuestions = essayList, isLoading = false)
                    }
                }
                .collect { result ->
                    if (result != null && result.isSubmitted) {
                        _uiState.update {
                            it.copy(
                                pgQuestions = pgList,
                                essayQuestions = essayList,
                                calculatedPgScore = result.pgScore,
                                isSubmitted = true,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                pgQuestions = pgList,
                                essayQuestions = essayList,
                                isLoading = false
                            )
                        }
                    }
                }
        }
    }

    fun selectPgAnswer(questionId: Int, optionIndex: Int) {
        _uiState.update { state ->
            val updatedMap = state.selectedPgAnswers.toMutableMap()
            updatedMap[questionId] = optionIndex
            state.copy(selectedPgAnswers = updatedMap)
        }
        val targetUserId = if (activeUserId == -1) 1 else activeUserId
        viewModelScope.launch {
            learningRepository?.saveAnswer(
                Answer(
                    userId = targetUserId,
                    questionId = "EVAL_PG_$questionId",
                    activityType = "EVALUATION_PG",
                    answerText = optionIndex.toString()
                )
            )
        }
    }

    fun updateEssayAnswer(questionId: Int, answerText: String) {
        _uiState.update { state ->
            val updatedMap = state.essayAnswers.toMutableMap()
            updatedMap[questionId] = answerText
            state.copy(essayAnswers = updatedMap)
        }
        val targetUserId = if (activeUserId == -1) 1 else activeUserId
        viewModelScope.launch {
            learningRepository?.saveAnswer(
                Answer(
                    userId = targetUserId,
                    questionId = "EVAL_ESSAY_$questionId",
                    activityType = "EVALUATION_ESSAY",
                    answerText = answerText
                )
            )
        }
    }

    fun navigatePg(index: Int) {
        if (index in 0 until _uiState.value.pgQuestions.size) {
            _uiState.update { it.copy(currentPgIndex = index) }
        }
    }

    fun navigateEssay(index: Int) {
        if (index in 0 until _uiState.value.essayQuestions.size) {
            _uiState.update { it.copy(currentEssayIndex = index) }
        }
    }

    fun submitEvaluation() {
        val targetUserId = if (activeUserId == -1) 1 else activeUserId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            var correctCount = 0

            state.pgQuestions.forEach { q ->
                val selected = state.selectedPgAnswers[q.id]
                if (selected == q.correctAnswerIndex) {
                    correctCount++
                }
            }

            val pgScore = if (state.pgQuestions.isNotEmpty()) {
                (correctCount.toDouble() / state.pgQuestions.size.toDouble()) * 100.0
            } else 0.0

            val evalResult = EvaluationResult(
                userId = targetUserId,
                pgScore = pgScore,
                isSubmitted = true
            )

            evaluationRepository.insertOrUpdateResult(evalResult)

            // Otomatis kirim nilai & rincian jawaban evaluasi ke Guru
            val user = activeUser ?: userRepository.getUserById(targetUserId)
            val submission = StudentSubmission(
                id = "SUB_EVAL_${targetUserId}",
                userId = targetUserId,
                studentName = user?.nama ?: "Siswa E-LKPD",
                studentClass = user?.kelas ?: "X E-1",
                studentNis = user?.nis ?: "NIS-$targetUserId",
                submissionType = SubmissionType.EVALUATION_HOTS,
                title = "Evaluasi Akhir HOTS Terintegrasi Parijoto",
                summary = "Skor PG: ${String.format(Locale.US, "%.1f", pgScore)} | ${state.essayAnswers.size} Jawaban Essay",
                payloadJson = formatEvaluationPayload(state, pgScore),
                score = pgScore.toFloat(),
                status = if (pgScore >= 75.0) "LOLOS_KKM" else "TERKIRIM",
                submittedAt = System.currentTimeMillis()
            )
            studentSubmissionRepository?.submitPayload(submission)

            _uiState.update {
                it.copy(
                    calculatedPgScore = pgScore,
                    isSubmitted = true,
                    isLoading = false
                )
            }
        }
    }

    private fun formatEvaluationPayload(state: EvaluationState, pgScore: Double): String {
        val pgAnswersJson = state.selectedPgAnswers.entries.joinToString(", ") { (qId, ans) ->
            """{"soalId": $qId, "jawabanPilihan": $ans}"""
        }
        val essayAnswersJson = state.essayAnswers.entries.joinToString(", ") { (qId, ans) ->
            """{"soalId": $qId, "jawabanUraian": "${escapeJson(ans)}"}"""
        }
        return """
            {
              "skor_pg": $pgScore,
              "jawaban_pg": [$pgAnswersJson],
              "jawaban_essay": [$essayAnswersJson]
            }
        """.trimIndent()
    }

    private fun escapeJson(input: String): String {
        return input.replace("\"", "\\\"").replace("\n", "\\n")
    }

    private fun getSamplePgQuestions(): List<PgQuestion> {
        return listOf(
            PgQuestion(
                id = 1,
                questionText = "Ekstrak buah Parijoto (Medinilla magnifica) menunjukkan warna merah terang saat dicampur dengan cuka dapur dan berubah menjadi kecokelatan ketika ditetesi larutan sabun. Berdasarkan fenomena tersebut, pigmen alami apakah yang bertanggung jawab atas perubahan warna indikator tersebut?",
                options = listOf("Klorofil", "Karotenoid", "Antosianin", "Xantofil", "Tanin"),
                correctAnswerIndex = 2,
                explanation = "Antosianin merupakan pigmen flavonoid peka pH yang berubah warna menjadi merah dalam suasana asam dan gelap/kebiruan dalam suasana basa."
            ),
            PgQuestion(
                id = 2,
                questionText = "Masyarakat Lereng Gunung Muria memanfaatkan rebusan air Parijoto untuk pengobatan alami. Secara sains ilmiah, khasiat kesehatan ini terutama disebabkan oleh tingginya kandungan senyawa fitokimia jenis...",
                options = listOf("Asam Lemak Jenuh", "Antioksidan Flavonoid", "Glukosa", "Natrium Klorida", "Kalsium Karbonat"),
                correctAnswerIndex = 1,
                explanation = "Buah Parijoto kaya akan flavonoid dan polifenol yang berfungsi sebagai antioksidan alami penangkal radikal bebas."
            ),
            PgQuestion(
                id = 3,
                questionText = "Seorang siswa menguji pH sampel tanah perkebunan Parijoto menggunakan indikator ekstrak Parijoto. Hasil pengujian menunjukkan warna merah pekat. Hal ini menandakan bahwa tanah perkebunan bersifat...",
                options = listOf("Basa Kuat", "Basa Lemah", "Netral", "Asam", "Sangat Basa"),
                correctAnswerIndex = 3,
                explanation = "Warna merah pekat indikator antosianin Parijoto mengindikasikan suasana larutan/tanah bersifat asam (pH < 7)."
            ),
            PgQuestion(
                id = 4,
                questionText = "Proses rekonstruksi pengetahuan asli (indigenous science) masyarakat Muria tentang Parijoto menjadi pengetahuan ilmiah (scientific science) bertujuan untuk...",
                options = listOf(
                    "Menghilangkan mitos lokal secara total",
                    "Menguji dan menjelaskan kepercayaan tradisional melalui bukti rasional dan eksperimen biokimia",
                    "Mengganti seluruh obat modern dengan tanaman herbal",
                    "Menurunkan nilai jual tanaman Parijoto di pasaran",
                    "Membatasi pemanfaatan Parijoto hanya untuk penelitian"
                ),
                correctAnswerIndex = 1,
                explanation = "Rekonstruksi etnosains bertujuan mentransformasi pengetahuan kearifan lokal menjadi sains ilmiah yang terverifikasi secara empiris."
            ),
            PgQuestion(
                id = 5,
                questionText = "Mengapa ekstrak Parijoto dapat dimanfaatkan sebagai indikator alternatif alami pengganti kertas lakmus di laboratorium sekolah?",
                options = listOf(
                    "Karena harganya sangat mahal",
                    "Karena memiliki trayek perubahan warna yang jelas terhadap tingkat keasaman (pH)",
                    "Karena ekstraknya bersifat beracun",
                    "Karena tidak bisa kedaluwarsa",
                    "Karena dapat mengendapkan semua jenis garam"
                ),
                correctAnswerIndex = 1,
                explanation = "Indikator alami yang efektif harus memiliki trayek perubahan warna sensitif yang membedakan kondisi asam, netral, dan basa."
            )
        )
    }

    private fun getSampleEssayQuestions(): List<EssayQuestion> {
        return listOf(
            EssayQuestion(
                id = 101,
                questionText = "Analisislah hubungan antara struktur senyawa Antosianin pada buah Parijoto dengan tingkat keasaman (pH) suatu larutan! Mengapa terjadi perubahan warna saat terjadi reaksi dengan asam dan basa?",
                rubricText = "Skor Maksimal: Menguraikan pergeseran struktur kation flavilium dalam suasana asam dan struktur quinonoid/basa."
            ),
            EssayQuestion(
                id = 102,
                questionText = "Rancanglah sebuah eksperimen sederhana berbasis etnosains Parijoto yang dapat diterapkan di masyarakat lereng Gunung Muria untuk menguji tingkat keasaman air minum lokal secara mandiri!",
                rubricText = "Skor Maksimal: Mencakup alat & bahan lokal, langkah kerja yang runtut, variabel kontrol, serta kriteria interpretasi warna."
            )
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                EvaluationViewModel(
                    userRepository = RepositoryModule.provideUserRepository(app),
                    evaluationRepository = RepositoryModule.provideEvaluationRepository(app),
                    learningRepository = RepositoryModule.provideLearningRepository(app),
                    studentSubmissionRepository = RepositoryModule.provideStudentSubmissionRepository(app)
                )
            }
        }
    }
}
