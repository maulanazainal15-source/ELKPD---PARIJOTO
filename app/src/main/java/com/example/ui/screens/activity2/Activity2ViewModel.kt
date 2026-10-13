package com.example.ui.screens.activity2

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

class Activity2ViewModel(
    private val userRepository: UserRepository,
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(Activity2State())
    val uiState: StateFlow<Activity2State> = _uiState.asStateFlow()

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
                        it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat data pengguna")
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
            val isRead = learningRepository.getAnswerByQuestion(userId, "ACT2_PROC_READ")?.answerText == "true"
            val indNote = learningRepository.getAnswerByQuestion(userId, "ACT2_INDIGENOUS")?.answerText.orEmpty()
            val sciExp = learningRepository.getAnswerByQuestion(userId, "ACT2_SCIENTIFIC")?.answerText.orEmpty()
            val disc = learningRepository.getAnswerByQuestion(userId, "ACT2_DISCUSSION")?.answerText.orEmpty()
            val conc = learningRepository.getAnswerByQuestion(userId, "ACT2_CONCLUSION")?.answerText.orEmpty()

            val currentObs = _uiState.value.observations.mapIndexed { idx, defaultRow ->
                val raw = learningRepository.getAnswerByQuestion(userId, "ACT2_OBS_$idx")?.answerText.orEmpty()
                val parts = raw.split("|")
                if (parts.size >= 3) {
                    defaultRow.copy(
                        warnaAwal = parts[0],
                        warnaSetelahReaksi = parts[1],
                        phPerkiraan = parts[2]
                    )
                } else {
                    defaultRow
                }
            }

            _uiState.update {
                it.copy(
                    isProcedureRead = isRead || it.isProcedureRead,
                    indigenousNote = indNote.ifBlank { it.indigenousNote },
                    scientificExplanation = sciExp.ifBlank { it.scientificExplanation },
                    observations = currentObs,
                    discussionAnswer = disc.ifBlank { it.discussionAnswer },
                    conclusionAnswer = conc.ifBlank { it.conclusionAnswer }
                )
            }
        }
    }

    private fun loadUserProgress(userId: Int) {
        viewModelScope.launch {
            learningRepository.getProgressByUserAndActivity(userId, "ACT_2")
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

    fun navigateToStep(step: Int) {
        if (step in 1..5) {
            _uiState.update { it.copy(currentStep = step) }
        }
    }

    fun updateProcedureReadStatus(isRead: Boolean) {
        _uiState.update { it.copy(isProcedureRead = isRead, isSaved = false) }
    }

    fun updateReconstruction(indigenous: String, scientific: String) {
        _uiState.update {
            it.copy(
                indigenousNote = indigenous,
                scientificExplanation = scientific,
                isSaved = false
            )
        }
    }

    fun updateObservationRow(index: Int, updatedRow: ObservationRow) {
        _uiState.update { state ->
            val updatedList = state.observations.toMutableList()
            if (index in updatedList.indices) {
                updatedList[index] = updatedRow
            }
            state.copy(observations = updatedList, isSaved = false)
        }
    }

    fun updateDiscussionAnswer(answer: String) {
        _uiState.update { it.copy(discussionAnswer = answer, isSaved = false) }
    }

    fun updateConclusionAnswer(answer: String) {
        _uiState.update { it.copy(conclusionAnswer = answer, isSaved = false) }
    }

    fun saveProgress() {
        val targetUserId = if (activeUserId == -1) 1 else activeUserId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value

            val answersToSave = mutableListOf(
                "ACT2_PROC_READ" to state.isProcedureRead.toString(),
                "ACT2_INDIGENOUS" to state.indigenousNote,
                "ACT2_SCIENTIFIC" to state.scientificExplanation,
                "ACT2_DISCUSSION" to state.discussionAnswer,
                "ACT2_CONCLUSION" to state.conclusionAnswer
            )
            state.observations.forEachIndexed { idx, row ->
                answersToSave.add("ACT2_OBS_$idx" to "${row.warnaAwal}|${row.warnaSetelahReaksi}|${row.phPerkiraan}")
            }

            answersToSave.forEach { (qId, text) ->
                learningRepository.saveAnswer(
                    Answer(
                        userId = targetUserId,
                        questionId = qId,
                        activityType = "ACTIVITY_2",
                        answerText = text
                    )
                )
            }

            var completedCount = 0

            if (state.isProcedureRead) completedCount++
            if (state.indigenousNote.isNotBlank() || state.scientificExplanation.isNotBlank()) completedCount++
            if (state.observations.any { it.warnaAwal.isNotBlank() || it.warnaSetelahReaksi.isNotBlank() }) completedCount++
            if (state.discussionAnswer.isNotBlank()) completedCount++
            if (state.conclusionAnswer.isNotBlank()) completedCount++

            val percentage = (completedCount * 100) / 5

            val progress = LearningProgress(
                userId = targetUserId,
                activityId = "ACT_2",
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
                Activity2ViewModel(
                    userRepository = RepositoryModule.provideUserRepository(app),
                    learningRepository = RepositoryModule.provideLearningRepository(app)
                )
            }
        }
    }
}
