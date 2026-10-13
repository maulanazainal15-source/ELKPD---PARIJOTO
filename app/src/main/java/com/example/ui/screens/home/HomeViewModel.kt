package com.example.ui.screens.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.repository.EvaluationRepository
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val learningRepository: LearningRepository,
    private val evaluationRepository: EvaluationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            userRepository.getAllUsers()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat data pengguna")
                    }
                }
                .collect { users ->
                    val activeUser = users.firstOrNull()
                    if (activeUser != null) {
                        observeUserProgress(activeUser.id)
                        _uiState.update { it.copy(currentUser = activeUser, isLoading = false) }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "Sesi pengguna tidak ditemukan")
                        }
                    }
                }
        }
    }

    private fun observeUserProgress(userId: Int) {
        viewModelScope.launch {
            combine(
                learningRepository.getAllProgressByUser(userId),
                evaluationRepository.getResultByUser(userId)
            ) { progressList, evalResult ->
                val act1Prog = progressList
                    .filter { it.activityId == "ACT_1" || it.activityId == "AKTIVITAS_1" }
                    .maxOfOrNull { it.percentage } ?: 0

                val act2Prog = progressList
                    .filter { it.activityId == "ACT_2" || it.activityId == "AKTIVITAS_2" }
                    .maxOfOrNull { it.percentage } ?: 0

                val evalProg = if (evalResult != null && evalResult.isSubmitted) 100 else 0
                val totalOverall = ((act1Prog + act2Prog + evalProg) / 3)

                HomeState(
                    currentUser = _uiState.value.currentUser,
                    activity1Progress = act1Prog,
                    activity2Progress = act2Prog,
                    evaluationProgress = evalProg,
                    overallProgress = totalOverall,
                    isEvaluationCompleted = evalResult?.isSubmitted ?: false,
                    isLoading = false
                )
            }.collect { updatedState ->
                _uiState.update { current ->
                    current.copy(
                        activity1Progress = updatedState.activity1Progress,
                        activity2Progress = updatedState.activity2Progress,
                        evaluationProgress = updatedState.evaluationProgress,
                        overallProgress = updatedState.overallProgress,
                        isEvaluationCompleted = updatedState.isEvaluationCompleted
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                HomeViewModel(
                    userRepository = RepositoryModule.provideUserRepository(app),
                    learningRepository = RepositoryModule.provideLearningRepository(app),
                    evaluationRepository = RepositoryModule.provideEvaluationRepository(app)
                )
            }
        }
    }
}
