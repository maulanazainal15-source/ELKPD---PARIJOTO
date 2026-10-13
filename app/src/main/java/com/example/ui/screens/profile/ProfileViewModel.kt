package com.example.ui.screens.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.repository.AdminPolicyRepository
import com.example.domain.repository.EvaluationRepository
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val learningRepository: LearningRepository,
    private val evaluationRepository: EvaluationRepository,
    private val adminPolicyRepository: AdminPolicyRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    private var activeUserId: Int = -1

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val policyFlow = adminPolicyRepository?.observePolicy() ?: flowOf(AdminRoleAccessPolicy())

            combine(
                userRepository.getAllUsers(),
                policyFlow
            ) { users, policy ->
                users to policy
            }
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat profil pengguna")
                    }
                }
                .collect { (users, policy) ->
                    val activeUser = users.firstOrNull()
                    if (activeUser != null) {
                        activeUserId = activeUser.id
                        _uiState.update {
                            it.copy(
                                user = activeUser,
                                policy = policy,
                                editedName = activeUser.fullName,
                                editedSchool = activeUser.schoolName,
                                editedClass = activeUser.className
                            )
                        }
                        observeStudentAnalytics(activeUser.id)
                    } else {
                        _uiState.update {
                            it.copy(
                                policy = policy,
                                isLoading = false,
                                errorMessage = "Sesi pengguna tidak ditemukan"
                            )
                        }
                    }
                }
        }
    }

    private fun observeStudentAnalytics(userId: Int) {
        viewModelScope.launch {
            combine(
                learningRepository.getProgressByUserAndActivity(userId, "ACT_1"),
                learningRepository.getProgressByUserAndActivity(userId, "ACT_2"),
                evaluationRepository.getResultByUser(userId)
            ) { act1Progress, act2Progress, evalResult ->

                val act1 = act1Progress?.percentage ?: 0
                val act2 = act2Progress?.percentage ?: 0
                val overall = ((act1 + act2) / 2)

                var completedCount = 0
                if (act1 >= 100) completedCount++
                if (act2 >= 100) completedCount++

                StudentAnalytics(
                    act1Percentage = act1,
                    act2Percentage = act2,
                    overallLearningPercentage = overall,
                    hotsPgScore = evalResult?.pgScore ?: 0.0,
                    isHotsSubmitted = evalResult?.isSubmitted ?: false,
                    completedActivitiesCount = completedCount,
                    totalActivitiesCount = 2
                )
            }.catch { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat statistik")
                }
            }.collect { analyticsData ->
                _uiState.update {
                    it.copy(
                        analytics = analyticsData,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun showEditDialog(show: Boolean) {
        _uiState.update { it.copy(isEditDialogVisible = show) }
    }

    fun showResetDialog(show: Boolean) {
        _uiState.update { it.copy(isResetDialogVisible = show) }
    }

    fun showAdminPinDialog(show: Boolean) {
        _uiState.update { it.copy(isAdminPinDialogVisible = show) }
    }

    fun verifyAdminPin(pinInput: String): Boolean {
        val policy = _uiState.value.policy
        val valid = !policy.requireAdminPinForLogin || pinInput.trim() == policy.adminPin.trim()
        if (!valid) {
            _uiState.update {
                it.copy(errorMessage = "PIN Admin tidak sesuai ketentuan Dashboard Admin (PIN aktif: ${policy.adminPin})")
            }
        } else {
            _uiState.update { it.copy(isAdminPinDialogVisible = false) }
        }
        return valid
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(editedName = newName) }
    }

    fun onSchoolChange(newSchool: String) {
        _uiState.update { it.copy(editedSchool = newSchool) }
    }

    fun onClassChange(newClass: String) {
        _uiState.update { it.copy(editedClass = newClass) }
    }

    fun updateProfile() {
        val currentUser = _uiState.value.user ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val newName = _uiState.value.editedName.ifBlank { currentUser.fullName }
            val newSchool = _uiState.value.editedSchool.ifBlank { currentUser.schoolName }
            val newClass = _uiState.value.editedClass.ifBlank { currentUser.className }

            val updatedUser = currentUser.copy(
                nama = newName,
                kelompok = newSchool,
                kelas = newClass,
                fullName = newName,
                schoolName = newSchool,
                className = newClass
            )

            userRepository.insertOrUpdateStudent(updatedUser)

            _uiState.update {
                it.copy(
                    user = updatedUser,
                    isEditDialogVisible = false,
                    isLoading = false,
                    successMessage = "Profil berhasil diperbarui"
                )
            }
        }
    }

    fun resetAllProgress() {
        if (activeUserId == -1) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            learningRepository.clearAllProgressByUser(activeUserId)
            evaluationRepository.clearResultByUser(activeUserId)

            _uiState.update {
                it.copy(
                    isResetDialogVisible = false,
                    isLoading = false,
                    successMessage = "Seluruh progres belajar berhasil Direset"
                )
            }
            loadProfileData()
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                ProfileViewModel(
                    userRepository = RepositoryModule.provideUserRepository(app),
                    learningRepository = RepositoryModule.provideLearningRepository(app),
                    evaluationRepository = RepositoryModule.provideEvaluationRepository(app),
                    adminPolicyRepository = RepositoryModule.provideAdminPolicyRepository(app)
                )
            }
        }
    }
}
