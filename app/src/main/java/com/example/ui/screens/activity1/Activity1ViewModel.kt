package com.example.ui.screens.activity1

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.model.Answer
import com.example.domain.model.LearningProgress
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Activity1ViewModel(
    private val userRepository: UserRepository,
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(Activity1State())
    val uiState: StateFlow<Activity1State> = _uiState.asStateFlow()

    private var activeUserId: Int = -1

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.getAllStudents()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat data siswa")
                    }
                }
                .collect { users ->
                    val activeUser = users.lastOrNull()
                    if (activeUser != null) {
                        activeUserId = activeUser.id
                        loadSavedAnswers(activeUser.id)
                        loadUserProgress(activeUser.id)
                    } else {
                        activeUserId = 1
                        loadSavedAnswers(1)
                        loadUserProgress(1)
                    }
                }
        }
    }

    private fun loadSavedAnswers(userId: Int) {
        viewModelScope.launch {
            val a1 = learningRepository.getAnswerByQuestion(userId, "ACT1_PHASE_1")?.answerText.orEmpty()
            val a2 = learningRepository.getAnswerByQuestion(userId, "ACT1_PHASE_2")?.answerText.orEmpty()
            val a3 = learningRepository.getAnswerByQuestion(userId, "ACT1_PHASE_3")?.answerText.orEmpty()
            val a4 = learningRepository.getAnswerByQuestion(userId, "ACT1_PHASE_4")?.answerText.orEmpty()
            val a5 = learningRepository.getAnswerByQuestion(userId, "ACT1_PHASE_5")?.answerText.orEmpty()
            _uiState.update {
                it.copy(
                    orientasiAnswer = a1.ifBlank { it.orientasiAnswer },
                    organisasiAnswer = a2.ifBlank { it.organisasiAnswer },
                    penyelidikanAnswer = a3.ifBlank { it.penyelidikanAnswer },
                    karyaAnswer = a4.ifBlank { it.karyaAnswer },
                    evaluasiAnswer = a5.ifBlank { it.evaluasiAnswer }
                )
            }
        }
    }

    private fun loadUserProgress(userId: Int) {
        viewModelScope.launch {
            learningRepository.getProgressByUserAndActivity(userId, "ACT_1")
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat progres")
                    }
                }
                .collect { progress ->
                    if (progress != null) {
                        _uiState.update {
                            it.copy(
                                progressPercentage = progress.percentage,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
        }
    }

    fun updateAnswer(phase: Int, answer: String) {
        _uiState.update { state ->
            when (phase) {
                1 -> state.copy(orientasiAnswer = answer, isSaved = false)
                2 -> state.copy(organisasiAnswer = answer, isSaved = false)
                3 -> state.copy(penyelidikanAnswer = answer, isSaved = false)
                4 -> state.copy(karyaAnswer = answer, isSaved = false)
                5 -> state.copy(evaluasiAnswer = answer, isSaved = false)
                else -> state
            }
        }
    }

    fun navigateToPhase(phase: Int) {
        if (phase in 1..5) {
            _uiState.update { it.copy(currentPhase = phase) }
        }
    }

    fun saveProgress() {
        val targetUserId = if (activeUserId == -1) 1 else activeUserId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val phaseAnswers = listOf(
                "ACT1_PHASE_1" to state.orientasiAnswer,
                "ACT1_PHASE_2" to state.organisasiAnswer,
                "ACT1_PHASE_3" to state.penyelidikanAnswer,
                "ACT1_PHASE_4" to state.karyaAnswer,
                "ACT1_PHASE_5" to state.evaluasiAnswer
            )

            phaseAnswers.forEach { (qId, text) ->
                learningRepository.saveAnswer(
                    Answer(
                        userId = targetUserId,
                        questionId = qId,
                        activityType = "ACTIVITY_1",
                        answerText = text
                    )
                )
            }

            val filledCount = phaseAnswers.count { it.second.isNotBlank() }
            val percentage = (filledCount * 100) / 5

            val progress = LearningProgress(
                userId = targetUserId,
                activityId = "ACT_1",
                percentage = percentage,
                isCompleted = percentage == 100
            )

            learningRepository.insertOrUpdateProgress(progress)

            _uiState.update {
                it.copy(
                    progressPercentage = percentage,
                    isLoading = false,
                    isSaved = true
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                Activity1ViewModel(
                    userRepository = RepositoryModule.provideUserRepository(app),
                    learningRepository = RepositoryModule.provideLearningRepository(app)
                )
            }
        }
    }
}
