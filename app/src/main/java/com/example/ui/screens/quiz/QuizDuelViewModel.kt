package com.example.ui.screens.quiz

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.di.DatabaseModule
import com.example.di.FirebaseModule
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LeaderboardEntry(
    val studentName: String,
    val score: Int,
    val rank: Int,
    val school: String = "SMA N 1 Gunung Muria",
    val isCurrentUser: Boolean = false
)

data class QuizQuestionItem(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

enum class DuelState {
    LOBBY,
    MATCHMAKING,
    IN_GAME,
    FINISHED
}

data class QuizDuelUiState(
    val state: DuelState = DuelState.LOBBY,
    val duelId: String? = null,
    val isHost: Boolean = true,
    val myName: String = "Siswa Muria",
    val opponentName: String = "Menunggu Lawan...",
    val myScore: Int = 0,
    val opponentScore: Int = 0,
    val currentQuestionIndex: Int = 0,
    val timerSeconds: Int = 15,
    val selectedOptionIndex: Int? = null,
    val isAnswerConfirmed: Boolean = false,
    val questions: List<QuizQuestionItem> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val isWinner: Boolean? = null,
    val isLoading: Boolean = false
)

class QuizDuelViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(QuizDuelUiState())
    val uiState: StateFlow<QuizDuelUiState> = _uiState.asStateFlow()

    private val firestore = try {
        FirebaseModule.provideFirestore(application)
    } catch (e: Exception) {
        null
    }

    private val auth = try {
        FirebaseModule.provideFirebaseAuth(application)
    } catch (e: Exception) {
        null
    }

    private var duelListener: ListenerRegistration? = null
    private var leaderboardListener: ListenerRegistration? = null
    private var timerJob: Job? = null
    private var botSimulationJob: Job? = null

    init {
        loadDefaultQuestions()
        loadLeaderboard()
        loadCurrentUserName()
    }

    private fun loadCurrentUserName() {
        val name = auth?.currentUser?.displayName ?: "Siswa Muria"
        _uiState.update { it.copy(myName = name) }
    }

    private fun loadDefaultQuestions() {
        val qList = listOf(
            QuizQuestionItem(
                id = 1,
                question = "Pelarut manakah yang paling efektif dan aman untuk mengekstraksi pigmen antosianin buah Parijoto?",
                options = listOf(
                    "Etanol 70% dengan penambahan sedikit asam sitrat",
                    "Air murni netral tanpa pengadukan",
                    "Minyak kelapa sawit suhu tinggi",
                    "Pelarut heksana murni nonpolar"
                ),
                correctIndex = 0,
                explanation = "Antosianin adalah senyawa polifenol polar yang sangat stabil dalam kondisi asam dan pelarut hidroalkohol."
            ),
            QuizQuestionItem(
                id = 2,
                question = "Perubahan warna antosianin Parijoto menjadi merah menyala terjadi pada suasana larutan...",
                options = listOf(
                    "Asam kuat (pH 1 - 3) menghasilkan kation flavilium",
                    "Basa kuat (pH 11 - 13) menghasilkan karboksilat",
                    "Netral (pH 7.0) menghasilkan ion amfoter",
                    "Sangat pekat menghasilkan endapan kalsium"
                ),
                correctIndex = 0,
                explanation = "Dalam suasana asam, antosianin berada dalam bentuk kation flavilium yang berwarna merah cerah stabil."
            ),
            QuizQuestionItem(
                id = 3,
                question = "Kearifan lokal masyarakat lereng Muria memanfaatkan buah Parijoto secara turun-temurun untuk...",
                options = listOf(
                    "Menjaga kesehatan janin & menangkal radikal bebas ibu hamil",
                    "Bahan pewarna sintetis tekstil industri berat",
                    "Pengawet kayu jati tahan rayap",
                    "Bahan bakar alternatif ramah lingkungan"
                ),
                correctIndex = 0,
                explanation = "Mitos dan budaya Kudus memercayai khasiat antioksidan flavonoid Parijoto untuk kesehatan ibu hamil."
            ),
            QuizQuestionItem(
                id = 4,
                question = "Pada sintaks Problem-Based Learning (PBL), investigasi larutan cuka dan kapur sirih masuk tahap...",
                options = listOf(
                    "Penyelidikan mandiri dan kelompok (Fase 3)",
                    "Orientasi masalah (Fase 1)",
                    "Evaluasi proses pemecahan masalah (Fase 5)",
                    "Mengorganisasikan siswa untuk belajar (Fase 2)"
                ),
                correctIndex = 0,
                explanation = "Praktikum uji asam-basa merupakan kegiatan eksperimen penyelidikan di Fase 3 PBL."
            ),
            QuizQuestionItem(
                id = 5,
                question = "Mengapa ekstrak Parijoto berubah warna menjadi hijau kecokelatan saat ditetesi larutan sabun/kapur?",
                options = listOf(
                    "Degradasi kuinoidal basa dan pembentukan kalkon",
                    "Reaksi pengendapan garam sulfat",
                    "Oksidasi klorofil sekunder",
                    "Polimerisasi etilen spontan"
                ),
                correctIndex = 0,
                explanation = "Pada pH basa, struktur antosianin bertransformasi menjadi basa kuinoidal dan cincin kalkon terbuka."
            )
        )
        _uiState.update { it.copy(questions = qList) }
    }

    private fun loadLeaderboard() {
        val fs = firestore
        if (fs != null) {
            leaderboardListener = fs.collection("users")
                .orderBy("hotsScore", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("QuizDuelVM", "Leaderboard listener error: ${error.message}")
                        setupDefaultLeaderboard()
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val currentUid = auth?.currentUser?.uid
                        val entries = snapshot.documents.mapIndexed { idx, doc ->
                            val name = doc.getString("nama") ?: "Siswa Muria"
                            val score = doc.getDouble("hotsScore")?.toInt() ?: (90 - idx * 5)
                            val school = doc.getString("sekolah") ?: "SMA N 1 Kudus"
                            val isMe = doc.id == currentUid
                            LeaderboardEntry(
                                studentName = name,
                                score = score,
                                rank = idx + 1,
                                school = school,
                                isCurrentUser = isMe
                            )
                        }
                        _uiState.update { it.copy(leaderboard = entries) }
                    } else {
                        setupDefaultLeaderboard()
                    }
                }
        } else {
            setupDefaultLeaderboard()
        }
    }

    private fun setupDefaultLeaderboard() {
        val defaultList = listOf(
            LeaderboardEntry("Dewi Sartika", 480, 1, "SMA N 1 Gunung Muria"),
            LeaderboardEntry("Ahmad Dahlan", 420, 2, "SMA N 2 Kudus"),
            LeaderboardEntry("Budi Santoso", 390, 3, "SMA N 1 Gebog"),
            LeaderboardEntry("Siti Aminah", 350, 4, "SMA N 1 Bae"),
            LeaderboardEntry("Rizky Pratama", 310, 5, "SMA N 1 Gunung Muria")
        )
        _uiState.update { it.copy(leaderboard = defaultList) }
    }

    fun startMatchmaking() {
        _uiState.update {
            it.copy(
                state = DuelState.MATCHMAKING,
                myScore = 0,
                opponentScore = 0,
                currentQuestionIndex = 0,
                timerSeconds = 15,
                selectedOptionIndex = null,
                isAnswerConfirmed = false,
                isWinner = null
            )
        }

        viewModelScope.launch {
            val fs = firestore
            val currentUid = auth?.currentUser?.uid ?: "player_${System.currentTimeMillis()}"
            val myName = _uiState.value.myName

            if (fs != null) {
                try {
                    // Cek antrean room WAITING
                    val waitingRooms = fs.collection("quiz_duels")
                        .whereEqualTo("status", "WAITING")
                        .limit(1)
                        .get()
                        .await()

                    if (!waitingRooms.isEmpty) {
                        // Join as Player 2
                        val roomDoc = waitingRooms.documents.first()
                        val roomId = roomDoc.id
                        val hostName = roomDoc.getString("player1Name") ?: "Lawan Muria"

                        fs.collection("quiz_duels").document(roomId).update(
                            "player2Id", currentUid,
                            "player2Name", myName,
                            "player2Score", 0,
                            "status", "IN_PROGRESS",
                            "updatedAt", FieldValue.serverTimestamp()
                        ).await()

                        _uiState.update {
                            it.copy(
                                state = DuelState.IN_GAME,
                                duelId = roomId,
                                isHost = false,
                                opponentName = hostName
                            )
                        }
                        listenToDuelRoom(roomId, isHost = false)
                        startQuestionTimer()
                        return@launch
                    } else {
                        // Create as Host (Player 1)
                        val newRoomId = "duel_${System.currentTimeMillis()}"
                        val roomData = hashMapOf(
                            "id" to newRoomId,
                            "player1Id" to currentUid,
                            "player1Name" to myName,
                            "player1Score" to 0,
                            "player2Id" to "",
                            "player2Name" to "Menunggu Lawan...",
                            "player2Score" to 0,
                            "status" to "WAITING",
                            "currentQuestionIndex" to 0,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                        fs.collection("quiz_duels").document(newRoomId).set(roomData).await()

                        _uiState.update {
                            it.copy(
                                duelId = newRoomId,
                                isHost = true
                            )
                        }
                        listenToDuelRoom(newRoomId, isHost = true)
                    }
                } catch (e: Exception) {
                    Log.w("QuizDuelVM", "Cloud matchmaking exception: ${e.message}")
                }
            }

            // Fallback Matchmaking Bot jika dalam 3.5 detik belum ada lawan manusia
            delay(3500L)
            if (_uiState.value.state == DuelState.MATCHMAKING) {
                val botNames = listOf("Laksmi (SMA 1 Kudus)", "Fajar (SMA 2 Gebog)", "Fitri (Kudus Etnosains)")
                val pickedBot = botNames.random()
                _uiState.update {
                    it.copy(
                        state = DuelState.IN_GAME,
                        opponentName = pickedBot
                    )
                }
                startQuestionTimer()
                simulateBotActivity()
            }
        }
    }

    private fun listenToDuelRoom(roomId: String, isHost: Boolean) {
        val fs = firestore ?: return
        duelListener?.remove()
        duelListener = fs.collection("quiz_duels").document(roomId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

                val status = snapshot.getString("status") ?: "WAITING"
                val p1Score = snapshot.getLong("player1Score")?.toInt() ?: 0
                val p2Score = snapshot.getLong("player2Score")?.toInt() ?: 0
                val p2Name = snapshot.getString("player2Name") ?: "Lawan Muria"

                if (status == "IN_PROGRESS" && _uiState.value.state == DuelState.MATCHMAKING) {
                    _uiState.update {
                        it.copy(
                            state = DuelState.IN_GAME,
                            opponentName = if (isHost) p2Name else it.opponentName
                        )
                    }
                    startQuestionTimer()
                }

                _uiState.update { current ->
                    current.copy(
                        opponentScore = if (isHost) p2Score else p1Score
                    )
                }
            }
    }

    private fun simulateBotActivity() {
        botSimulationJob?.cancel()
        botSimulationJob = viewModelScope.launch {
            while (_uiState.value.state == DuelState.IN_GAME) {
                delay((3000..6000).random().toLong())
                if (_uiState.value.state == DuelState.IN_GAME) {
                    val addPoints = listOf(60, 80, 90, 100).random()
                    _uiState.update { it.copy(opponentScore = it.opponentScore + addPoints) }
                }
            }
        }
    }

    private fun startQuestionTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerSeconds = 15, selectedOptionIndex = null, isAnswerConfirmed = false) }

        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0 && _uiState.value.state == DuelState.IN_GAME) {
                delay(1000L)
                _uiState.update { it.copy(timerSeconds = (it.timerSeconds - 1).coerceAtLeast(0)) }
            }

            // Waktu habis pada soal saat ini
            if (_uiState.value.state == DuelState.IN_GAME) {
                nextQuestionOrFinish()
            }
        }
    }

    fun selectAnswer(optionIndex: Int) {
        if (_uiState.value.isAnswerConfirmed) return

        val state = _uiState.value
        val currentQ = state.questions.getOrNull(state.currentQuestionIndex) ?: return
        val isCorrect = optionIndex == currentQ.correctIndex

        val scoreIncrement = if (isCorrect) {
            val timeBonus = (state.timerSeconds * 6).coerceIn(10, 90)
            100 + timeBonus
        } else {
            0
        }

        val newScore = state.myScore + scoreIncrement

        _uiState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isAnswerConfirmed = true,
                myScore = newScore
            )
        }

        // Sinkronkan skor saya ke cloud Firestore
        viewModelScope.launch {
            val fs = firestore
            val duelId = state.duelId
            if (fs != null && duelId != null) {
                try {
                    val field = if (state.isHost) "player1Score" else "player2Score"
                    fs.collection("quiz_duels").document(duelId).update(
                        field, newScore,
                        "updatedAt", FieldValue.serverTimestamp()
                    ).await()
                } catch (e: Exception) {
                    Log.w("QuizDuelVM", "Failed to sync duel score: ${e.message}")
                }
            }
        }

        // Pindah ke soal berikutnya setelah jeda visual 1.2 detik
        viewModelScope.launch {
            delay(1200L)
            nextQuestionOrFinish()
        }
    }

    private fun nextQuestionOrFinish() {
        val state = _uiState.value
        if (state.currentQuestionIndex < state.questions.size - 1) {
            _uiState.update {
                it.copy(currentQuestionIndex = it.currentQuestionIndex + 1)
            }
            startQuestionTimer()
        } else {
            finishDuel()
        }
    }

    private fun finishDuel() {
        timerJob?.cancel()
        botSimulationJob?.cancel()

        val state = _uiState.value
        val isWin = state.myScore >= state.opponentScore

        _uiState.update {
            it.copy(
                state = DuelState.FINISHED,
                isWinner = isWin
            )
        }

        viewModelScope.launch {
            // Update Firestore room
            val fs = firestore
            val duelId = state.duelId
            if (fs != null && duelId != null) {
                try {
                    fs.collection("quiz_duels").document(duelId).update(
                        "status", "COMPLETED",
                        "updatedAt", FieldValue.serverTimestamp()
                    ).await()
                } catch (e: Exception) {
                    Log.w("QuizDuelVM", "Failed to update duel completion: ${e.message}")
                }
            }

            // Simpan riwayat skor kuis ke profil cloud siswa untuk kalkulasi Indeks HOTS terpusat
            val uid = auth?.currentUser?.uid
            if (fs != null && uid != null) {
                try {
                    fs.collection("users").document(uid).update(
                        "hotsScore", FieldValue.increment(state.myScore.toDouble() / 10.0),
                        "lastQuizScore", state.myScore,
                        "updatedAt", FieldValue.serverTimestamp()
                    ).await()
                } catch (e: Exception) {
                    Log.w("QuizDuelVM", "Failed to update user hotsScore in profile: ${e.message}")
                }
            }
        }
    }

    fun returnToLobby() {
        timerJob?.cancel()
        botSimulationJob?.cancel()
        duelListener?.remove()
        _uiState.update {
            it.copy(
                state = DuelState.LOBBY,
                myScore = 0,
                opponentScore = 0,
                currentQuestionIndex = 0,
                timerSeconds = 15,
                selectedOptionIndex = null,
                isAnswerConfirmed = false,
                isWinner = null,
                duelId = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        botSimulationJob?.cancel()
        duelListener?.remove()
        leaderboardListener?.remove()
    }
}
