package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiAiService
import com.example.database.ElpkdDatabase
import com.example.domain.ElpkdCurriculumData
import com.example.model.AnswerEntity
import com.example.model.ImageEntity
import com.example.model.ProgressEntity
import com.example.model.ResultEntity
import com.example.model.TeacherConfigEntity
import com.example.model.UserEntity
import com.example.repository.ElpkdRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainNavTab(val label: String) {
    BERANDA("Beranda"),
    AKTIVITAS("Aktivitas"),
    EVALUASI("Evaluasi"),
    HASIL("Hasil"),
    PROFIL("Profil")
}

enum class ActivitySubTab(val label: String, val pblPhase: Int) {
    PENDAHULUAN("Pendahuluan", 1),
    CP_TP("CP & TP", 1),
    AKTIVITAS_1("Aktivitas 1", 2),
    AKTIVITAS_2("Aktivitas 2", 3)
}

@OptIn(ExperimentalCoroutinesApi::class)
class ElpkdViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ElpkdRepository

    init {
        val dao = ElpkdDatabase.getDatabase(application).elpkdDao()
        repository = ElpkdRepository(dao)
        seedInitialDataIfEmpty()
    }

    // Navigation & UI State
    private val _showSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean> = _showSplash.asStateFlow()

    private val _selectedMainTab = MutableStateFlow(MainNavTab.BERANDA)
    val selectedMainTab: StateFlow<MainNavTab> = _selectedMainTab.asStateFlow()

    private val _selectedActivitySubTab = MutableStateFlow(ActivitySubTab.PENDAHULUAN)
    val selectedActivitySubTab: StateFlow<ActivitySubTab> = _selectedActivitySubTab.asStateFlow()

    private val _isTeacherDashboardOpen = MutableStateFlow(false)
    val isTeacherDashboardOpen: StateFlow<Boolean> = _isTeacherDashboardOpen.asStateFlow()

    private val _isAiTutorSheetOpen = MutableStateFlow(false)
    val isAiTutorSheetOpen: StateFlow<Boolean> = _isAiTutorSheetOpen.asStateFlow()

    private val _aiTutorResponse = MutableStateFlow(
        "Halo! Saya AI Tutor Etnosains & Biologi Parijoto. Tanyakan konsep biologi, metabolit sekunder (flavonoid, saponin, tanin, β-sitosterol), mikroklimat Gunung Muria, atau minta pertanyaan pemantik untuk membantumu berdiskusi!"
    )
    val aiTutorResponse: StateFlow<String> = _aiTutorResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    // Active User & Lists
    val activeUser: StateFlow<UserEntity?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProgress: StateFlow<List<ProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnswers: StateFlow<List<AnswerEntity>> = repository.allAnswers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allResults: StateFlow<List<ResultEntity>> = repository.allResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teacherConfig: StateFlow<TeacherConfigEntity> = repository.teacherConfig
        .map { it ?: TeacherConfigEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeacherConfigEntity())

    // Active user scoped reactive streams
    val userProgressList: StateFlow<List<ProgressEntity>> = activeUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.getProgressForUser(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userAnswersMap: StateFlow<Map<String, AnswerEntity>> = activeUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyMap())
            else repository.getAnswersForUser(user.id).map { list -> list.associateBy { it.questionId } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val userImages: StateFlow<List<ImageEntity>> = activeUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.getImagesForUser(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userResults: StateFlow<List<ResultEntity>> = activeUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.getResultsForUser(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            repository.upsertTeacherConfig(TeacherConfigEntity())
        }
    }

    fun dismissSplash() {
        _showSplash.value = false
    }

    fun selectMainTab(tab: MainNavTab) {
        _selectedMainTab.value = tab
    }

    fun navigateToActivitySubTab(subTab: ActivitySubTab) {
        _selectedMainTab.value = MainNavTab.AKTIVITAS
        _selectedActivitySubTab.value = subTab
    }

    fun setTeacherDashboardOpen(open: Boolean) {
        _isTeacherDashboardOpen.value = open
    }

    fun setAiTutorSheetOpen(open: Boolean) {
        _isAiTutorSheetOpen.value = open
    }

    fun clearStatusMessage() {
        _statusBannerMessage.value = null
    }

    fun showStatusMessage(msg: String) {
        _statusBannerMessage.value = msg
    }

    // --- IDENTITAS PESERTA DIDIK ---
    fun saveStudentIdentity(
        nama: String,
        nis: String,
        kelas: String,
        kelompok: String,
        modeKerja: String
    ) {
        viewModelScope.launch {
            val current = activeUser.value
            val userToSave = UserEntity(
                id = current?.id ?: 0,
                nama = nama.trim(),
                nis = nis.trim(),
                kelas = kelas.trim(),
                kelompok = if (modeKerja == "INDIVIDU") "Mandiri (Individu)" else kelompok.trim(),
                modeKerja = modeKerja,
                role = "SISWA",
                isActive = true,
                timestamp = System.currentTimeMillis()
            )
            val savedUserId = repository.saveActiveUser(userToSave)
            // Initialize default progress items if not present
            val currentProg = userProgressList.value.associateBy { it.activityId }
            val sections = listOf("PENDAHULUAN", "CP_TP", "AKTIVITAS_1", "AKTIVITAS_2", "EVALUASI")
            for (sec in sections) {
                if (!currentProg.containsKey(sec)) {
                    repository.upsertProgress(
                        ProgressEntity(
                            userId = savedUserId,
                            activityId = sec,
                            status = "BELUM",
                            progress = 0
                        )
                    )
                }
            }
            _statusBannerMessage.value = "Identitas peserta didik berhasil disimpan secara lokal."
        }
    }

    fun switchUser(user: UserEntity) {
        viewModelScope.launch {
            repository.switchActiveUser(user)
            _statusBannerMessage.value = "Berpindah ke profil: ${user.nama}"
        }
    }

    fun createNewStudentProfile() {
        viewModelScope.launch {
            val newId = repository.saveActiveUser(
                UserEntity(
                    nama = "",
                    nis = "",
                    kelas = "X E-1",
                    kelompok = "Kelompok 1",
                    modeKerja = "KELOMPOK",
                    role = "SISWA",
                    isActive = true
                )
            )
            val sections = listOf("PENDAHULUAN", "CP_TP", "AKTIVITAS_1", "AKTIVITAS_2", "EVALUASI")
            for (sec in sections) {
                repository.upsertProgress(
                    ProgressEntity(userId = newId, activityId = sec, status = "BELUM", progress = 0)
                )
            }
        }
    }

    // --- PENYIMPANAN JAWABAN & AUTOSAVE PROGRESS ---
    fun saveAnswer(questionId: String, answerText: String) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            val existing = userAnswersMap.value[questionId]
            repository.upsertAnswer(
                AnswerEntity(
                    userId = user.id,
                    questionId = questionId,
                    answer = answerText,
                    aiFeedback = existing?.aiFeedback ?: "",
                    aiSuggestedScore = existing?.aiSuggestedScore,
                    teacherScore = existing?.teacherScore,
                    teacherNote = existing?.teacherNote ?: "",
                    isSynced = false,
                    timestamp = System.currentTimeMillis()
                )
            )
            recalculateProgress(user.id)
        }
    }

    fun markSectionCompleted(activityId: String) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.upsertProgress(
                ProgressEntity(
                    userId = user.id,
                    activityId = activityId,
                    status = "SELESAI",
                    progress = 100,
                    timestamp = System.currentTimeMillis()
                )
            )
            _statusBannerMessage.value = "Tahap $activityId selesai (100%)!"
        }
    }

    private suspend fun recalculateProgress(userId: Int) {
        val map = userAnswersMap.value

        // Aktivitas 1 memiliki 5 input utama
        val akt1Keys = listOf("akt1_k1_q1", "akt1_k1_q2", "akt1_k2_q1", "akt1_k2_q2", "akt1_k3_hipotesis")
        val akt1Filled = akt1Keys.count { (map[it]?.answer?.trim()?.length ?: 0) >= 5 }
        val akt1Pct = ((akt1Filled * 100f) / akt1Keys.size).toInt().coerceIn(0, 100)
        repository.upsertProgress(
            ProgressEntity(
                userId = userId,
                activityId = "AKTIVITAS_1",
                status = when {
                    akt1Pct == 100 -> "SELESAI"
                    akt1Pct > 0 -> "PROSES"
                    else -> "BELUM"
                },
                progress = akt1Pct
            )
        )

        // Aktivitas 2 memiliki 6 input esai + minimal 3 sel tabel dimensi + 1 foto morfologi
        val akt2EssayKeys = listOf(
            "akt2_rekonstruksi", "akt2_studi_ig", "akt2_senyawa",
            "akt2_kritis_1", "akt2_kritis_2", "akt2_kesimpulan"
        )
        val tableKeys = listOf(
            "tbl_row_1_lokal", "tbl_row_1_ilmiah", "tbl_row_1_manfaat",
            "tbl_row_2_lokal", "tbl_row_2_ilmiah", "tbl_row_2_budidaya",
            "tbl_row_3_lokal", "tbl_row_3_ilmiah", "tbl_row_3_indikator"
        )
        val filledEssays = akt2EssayKeys.count { (map[it]?.answer?.trim()?.length ?: 0) >= 5 }
        val filledTable = tableKeys.count { (map[it]?.answer?.trim()?.length ?: 0) >= 3 }.coerceAtMost(3)
        val hasImage = if (userImages.value.isNotEmpty()) 1 else 0
        val totalAkt2Items = 6 + 3 + 1 // 10 poin bobot
        val currentAkt2Score = filledEssays + filledTable + hasImage
        val akt2Pct = ((currentAkt2Score * 100f) / totalAkt2Items).toInt().coerceIn(0, 100)
        repository.upsertProgress(
            ProgressEntity(
                userId = userId,
                activityId = "AKTIVITAS_2",
                status = when {
                    akt2Pct == 100 -> "SELESAI"
                    akt2Pct > 0 -> "PROSES"
                    else -> "BELUM"
                },
                progress = akt2Pct
            )
        )

        // Evaluasi (5 PG + 4 Uraian = 9 soal)
        val evalKeys = listOf(
            "mcq_1", "mcq_2", "mcq_3", "mcq_4", "mcq_5",
            "essay_1", "essay_2", "essay_3", "essay_4"
        )
        val evalFilled = evalKeys.count { !map[it]?.answer.isNullOrBlank() }
        val evalPct = ((evalFilled * 100f) / evalKeys.size).toInt().coerceIn(0, 100)
        repository.upsertProgress(
            ProgressEntity(
                userId = userId,
                activityId = "EVALUASI",
                status = when {
                    evalPct == 100 -> "SELESAI"
                    evalPct > 0 -> "PROSES"
                    else -> "BELUM"
                },
                progress = evalPct
            )
        )
    }

    // --- UPLOAD & KELOLA GAMBAR MORFOLOGI PARIJOTO ---
    fun addMorphologyImage(filePath: String, caption: String, bagianTanaman: String) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.insertImage(
                ImageEntity(
                    userId = user.id,
                    filePath = filePath,
                    caption = caption.ifBlank { "Pengamatan Morfologi Parijoto (Medinilla speciosa)" },
                    bagianTanaman = bagianTanaman
                )
            )
            recalculateProgress(user.id)
            _statusBannerMessage.value = "Foto morfologi Parijoto berhasil disimpan!"
        }
    }

    fun deleteMorphologyImage(imageId: Int) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.deleteImageById(imageId)
            recalculateProgress(user.id)
            _statusBannerMessage.value = "Foto morfologi telah dihapus."
        }
    }

    // --- SUBMIT EVALUASI & PERHITUNGAN NILAI ---
    fun submitEvaluation(useAiAssistGrading: Boolean) {
        val user = activeUser.value ?: return
        val cfg = teacherConfig.value
        val answers = userAnswersMap.value

        viewModelScope.launch {
            _isAiLoading.value = true

            // 1. Hitung skor Pilihan Ganda HOTS
            val mcqs = ElpkdCurriculumData.defaultMcqQuestions
            var correctCount = 0
            for (q in mcqs) {
                val chosen = answers[q.id]?.answer?.trim()
                if (chosen == q.correctOption) {
                    correctCount++
                }
            }
            val mcqScore = (correctCount * cfg.pointsPerMcq).toFloat().coerceIn(0f, 100f)

            // 2. Soal Uraian: Jangan otomatis memberikan nilai final jika belum ada AI grading atau nilai Guru
            var calculatedEssayScore: Float? = null
            var gradingStatus = "MENUNGGU_GURU"

            if (useAiAssistGrading && cfg.isAiEnabled) {
                val essays = ElpkdCurriculumData.defaultEssayQuestions
                var sumAi = 0
                for (eq in essays) {
                    val studentAns = answers[eq.id]?.answer ?: ""
                    val (score, feedback) = GeminiAiService.evaluateAnswerWithRubric(
                        questionText = eq.question,
                        studentAnswer = studentAns,
                        rubricFocus = eq.rubricFocus
                    )
                    sumAi += score
                    val existing = answers[eq.id]
                    repository.upsertAnswer(
                        AnswerEntity(
                            userId = user.id,
                            questionId = eq.id,
                            answer = studentAns,
                            aiFeedback = feedback,
                            aiSuggestedScore = score,
                            teacherScore = existing?.teacherScore,
                            teacherNote = existing?.teacherNote ?: "",
                            isSynced = true
                        )
                    )
                }
                calculatedEssayScore = (sumAi.toFloat() / essays.size).coerceIn(0f, 100f)
                gradingStatus = "DINILAI_AI"
            }

            // Cek apakah guru sudah pernah memberi skor manual sebelumnya
            val existingTeacherScores = ElpkdCurriculumData.defaultEssayQuestions.mapNotNull {
                answers[it.id]?.teacherScore
            }
            if (existingTeacherScores.size == ElpkdCurriculumData.defaultEssayQuestions.size) {
                calculatedEssayScore = existingTeacherScores.average().toFloat()
                gradingStatus = "DINILAI_GURU"
            }

            val finalCombinedScore = if (calculatedEssayScore != null) {
                ((mcqScore * cfg.mcqWeightPercent) + (calculatedEssayScore * cfg.essayWeightPercent)) / 100f
            } else {
                // Jika uraian belum dinilai, tampilkan kontribusi nilai PG sesuai bobot atau nilai sementara PG
                mcqScore * (cfg.mcqWeightPercent / 100f)
            }

            repository.insertResult(
                ResultEntity(
                    userId = user.id,
                    namaSiswa = user.nama.ifBlank { "Peserta Didik" },
                    nisSiswa = user.nis.ifBlank { "-" },
                    kelasSiswa = user.kelas,
                    kelompokSiswa = user.kelompok,
                    mcqCorrectCount = correctCount,
                    mcqTotalCount = mcqs.size,
                    mcqScore = mcqScore,
                    essayScore = calculatedEssayScore,
                    finalScore = finalCombinedScore,
                    gradingMode = gradingStatus,
                    rubricSummaryJson = "Bobot PG ${cfg.mcqWeightPercent}% | Uraian ${cfg.essayWeightPercent}%",
                    teacherFeedback = if (gradingStatus == "DINILAI_AI") {
                        "Penilaian AI bersifat bantuan dan dapat ditinjau kembali oleh guru."
                    } else {
                        "Menunggu verifikasi dan penilaian uraian oleh Guru."
                    }
                )
            )

            repository.upsertProgress(
                ProgressEntity(
                    userId = user.id,
                    activityId = "EVALUASI",
                    status = "SELESAI",
                    progress = 100
                )
            )

            _isAiLoading.value = false
            _selectedMainTab.value = MainNavTab.HASIL
            _statusBannerMessage.value = "Evaluasi berhasil disimpan! Lihat rincian nilai pada halaman Hasil."
        }
    }

    // --- FITUR GURU: PENILAIAN RUBRIK MANUAL & KONFIGURASI ---
    fun submitTeacherManualGrade(
        result: ResultEntity,
        rubricScores1To4: List<Int>, // 6 aspek rubrik skala 1-4
        teacherFeedbackText: String
    ) {
        val cfg = teacherConfig.value
        viewModelScope.launch {
            val weights = listOf(
                cfg.weightKonsepBiologi,
                cfg.weightPenggunaanData,
                cfg.weightKemampuanAnalisis,
                cfg.weightBerpikirKritis,
                cfg.weightSolusiKonservasi,
                cfg.weightKomunikasi
            )
            val totalWeight = weights.sum().coerceAtLeast(1)
            var weightedSum = 0f
            for (i in rubricScores1To4.indices) {
                val scale100 = (rubricScores1To4[i].coerceIn(1, 4) / 4f) * 100f
                weightedSum += scale100 * (weights.getOrElse(i) { 15 } / totalWeight.toFloat())
            }
            val finalScore = ((result.mcqScore * cfg.mcqWeightPercent) + (weightedSum * cfg.essayWeightPercent)) / 100f

            repository.updateResult(
                result.copy(
                    essayScore = weightedSum,
                    finalScore = finalScore,
                    gradingMode = "DINILAI_GURU",
                    rubricSummaryJson = "Rubrik (1-4): Konsep=${rubricScores1To4.getOrNull(0)}, Data=${rubricScores1To4.getOrNull(1)}, Analisis=${rubricScores1To4.getOrNull(2)}, Kritis=${rubricScores1To4.getOrNull(3)}, Konservasi=${rubricScores1To4.getOrNull(4)}, Komunikasi=${rubricScores1To4.getOrNull(5)}",
                    teacherFeedback = teacherFeedbackText.ifBlank { "Telah dinilai dan diverifikasi oleh Guru berdasarkan rubrik 6 aspek." }
                )
            )
            _statusBannerMessage.value = "Nilai uraian untuk ${result.namaSiswa} berhasil diperbarui oleh Guru!"
        }
    }

    fun updateTeacherConfiguration(newConfig: TeacherConfigEntity) {
        viewModelScope.launch {
            repository.upsertTeacherConfig(newConfig)
            _statusBannerMessage.value = "Konfigurasi E-LKPD & Pengaturan Guru berhasil disimpan!"
        }
    }

    // --- FITUR AI TUTOR & AI FEEDBACK ---
    fun askAiTutorQuestion(question: String, contextPhase: String) {
        if (question.isBlank()) return
        viewModelScope.launch {
            _isAiLoading.value = true
            val reply = GeminiAiService.askAiTutor(question, contextPhase)
            _aiTutorResponse.value = reply
            _isAiLoading.value = false
        }
    }

    fun requestAiFeedbackForSingleAnswer(questionId: String, questionText: String, rubricFocus: String) {
        val user = activeUser.value ?: return
        val currentAns = userAnswersMap.value[questionId]?.answer ?: ""
        if (currentAns.isBlank()) {
            _statusBannerMessage.value = "Tuliskan jawaban terlebih dahulu sebelum meminta analisis AI."
            return
        }
        viewModelScope.launch {
            _isAiLoading.value = true
            val (score, feedback) = GeminiAiService.evaluateAnswerWithRubric(
                questionText = questionText,
                studentAnswer = currentAns,
                rubricFocus = rubricFocus
            )
            val existing = userAnswersMap.value[questionId]
            repository.upsertAnswer(
                AnswerEntity(
                    userId = user.id,
                    questionId = questionId,
                    answer = currentAns,
                    aiFeedback = feedback,
                    aiSuggestedScore = score,
                    teacherScore = existing?.teacherScore,
                    teacherNote = existing?.teacherNote ?: "",
                    isSynced = true
                )
            )
            _isAiLoading.value = false
            _statusBannerMessage.value = "Umpan balik AI selesai dibuat!"
        }
    }

    fun syncOfflineData() {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.markAllAnswersSynced(user.id)
            _statusBannerMessage.value = "Seluruh jawaban & progress lokal telah tersinkronisasi!"
        }
    }
}
