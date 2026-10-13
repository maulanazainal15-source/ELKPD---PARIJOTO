package com.example.ui.screens.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.model.User
import com.example.domain.model.UserMode
import com.example.domain.model.UserRole
import com.example.domain.repository.AdminPolicyRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IdentityViewModel(
    private val userRepository: UserRepository,
    private val adminPolicyRepository: AdminPolicyRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdentityState())
    val uiState: StateFlow<IdentityState> = _uiState.asStateFlow()

    init {
        observePolicyAndUsers()
    }

    private fun observePolicyAndUsers() {
        val policyFlow = adminPolicyRepository?.observePolicy()
        if (policyFlow != null) {
            viewModelScope.launch {
                combine(
                    policyFlow,
                    userRepository.getAllUsers()
                ) { policy, users ->
                    policy to users
                }.catch {
                    // Fallback to default policy if error
                }.collect { (policy, users) ->
                    val allowedClasses = policy.getAllowedClassesList()
                    val currentKelas = _uiState.value.kelas
                    val resolvedKelas = if (allowedClasses.isNotEmpty() && currentKelas !in allowedClasses) {
                        allowedClasses.first()
                    } else {
                        currentKelas
                    }
                    val resolvedMode = when {
                        !policy.allowIndividuMode && policy.allowKelompokMode -> UserMode.KELOMPOK
                        policy.allowIndividuMode && !policy.allowKelompokMode -> UserMode.INDIVIDU
                        else -> _uiState.value.mode
                    }
                    _uiState.update {
                        it.copy(
                            policy = policy,
                            existingUsers = users,
                            kelas = resolvedKelas,
                            mode = resolvedMode
                        )
                    }
                    validateForm()
                }
            }
        }
    }

    fun onNamaChanged(nama: String) {
        _uiState.update { it.copy(nama = nama, errorMessage = null) }
        validateForm()
    }

    fun onNisChanged(nis: String) {
        _uiState.update { it.copy(nis = nis, errorMessage = null) }
        validateForm()
    }

    fun onKelasChanged(kelas: String) {
        _uiState.update { it.copy(kelas = kelas, errorMessage = null) }
        validateForm()
    }

    fun onKelompokChanged(kelompok: String) {
        _uiState.update { it.copy(kelompok = kelompok, errorMessage = null) }
        validateForm()
    }

    fun onModeChanged(mode: UserMode) {
        val policy = _uiState.value.policy
        if (mode == UserMode.INDIVIDU && !policy.allowIndividuMode) return
        if (mode == UserMode.KELOMPOK && !policy.allowKelompokMode) return
        _uiState.update { it.copy(mode = mode, errorMessage = null) }
        validateForm()
    }

    fun onRoleChanged(role: UserRole) {
        _uiState.update { it.copy(role = role, errorMessage = null) }
        validateForm()
    }

    fun onAdminPinChanged(pin: String) {
        _uiState.update { it.copy(adminPinInput = pin, errorMessage = null) }
        validateForm()
    }

    fun onClassAccessCodeChanged(code: String) {
        _uiState.update { it.copy(classAccessCodeInput = code, errorMessage = null) }
        validateForm()
    }

    fun selectExistingUser(user: User, pinForTeacher: String = "") {
        val policy = _uiState.value.policy
        if (user.role == UserRole.TEACHER && policy.requireAdminPinForLogin) {
            if (pinForTeacher.trim() != policy.adminPin.trim()) {
                _uiState.update {
                    it.copy(errorMessage = "PIN Otorisasi Guru/Admin tidak sesuai ketentuan Dashboard Admin.")
                }
                return
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val updated = user.copy(createdTimestamp = System.currentTimeMillis())
            userRepository.registerOrLoginUser(updated)
            _uiState.update {
                it.copy(
                    nama = user.nama,
                    nis = user.nis,
                    kelas = user.kelas,
                    kelompok = user.kelompok,
                    mode = user.mode,
                    role = user.role,
                    isLoading = false,
                    isSuccessRegister = true
                )
            }
        }
    }

    private fun validateForm() {
        val state = _uiState.value
        val policy = state.policy
        val isValid = if (state.role == UserRole.STUDENT) {
            val baseValid = state.nama.isNotBlank() &&
                state.nis.isNotBlank() &&
                state.kelas.isNotBlank() &&
                (state.mode == UserMode.INDIVIDU || state.kelompok.isNotBlank())
            val codeValid = !policy.requireClassAccessCode || state.classAccessCodeInput.isNotBlank()
            baseValid && codeValid && policy.allowStudentSelfRegistration
        } else {
            val baseValid = state.nama.isNotBlank() && state.nis.isNotBlank()
            val pinValid = !policy.requireAdminPinForLogin || state.adminPinInput.isNotBlank()
            baseValid && pinValid
        }

        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun registerUser() {
        val state = _uiState.value
        val policy = state.policy
        if (!state.isFormValid) return

        // Validasi Ketentuan Dashboard Admin sebelum simpan
        if (state.role == UserRole.STUDENT) {
            if (!policy.allowStudentSelfRegistration) {
                _uiState.update {
                    it.copy(errorMessage = "Pendaftaran siswa mandiri sedang ditutup oleh Administrator.")
                }
                return
            }
            if (policy.requireClassAccessCode &&
                !state.classAccessCodeInput.trim().equals(policy.classAccessCode.trim(), ignoreCase = true)
            ) {
                _uiState.update {
                    it.copy(errorMessage = "Kode Akses Kelas tidak valid! Hubungi Guru/Admin untuk kode akses yang benar.")
                }
                return
            }
            val allowedClasses = policy.getAllowedClassesList()
            if (allowedClasses.isNotEmpty() && state.kelas.trim() !in allowedClasses) {
                _uiState.update {
                    it.copy(errorMessage = "Kelas '${state.kelas}' tidak termasuk dalam daftar kelas yang diizinkan Admin (${allowedClasses.joinToString()}).")
                }
                return
            }
        } else {
            // Validasi Ketentuan Peran Guru / Administrator
            if (policy.requireAdminPinForLogin && state.adminPinInput.trim() != policy.adminPin.trim()) {
                _uiState.update {
                    it.copy(errorMessage = "PIN Keamanan Guru/Admin salah! Masuk ke peran Guru/Administrator memerlukan PIN yang ditetapkan pada Dashboard Admin.")
                }
                return
            }
            if (policy.requireApprovedNipPrefix &&
                policy.approvedNipPrefix.isNotBlank() &&
                !state.nis.trim().startsWith(policy.approvedNipPrefix.trim())
            ) {
                _uiState.update {
                    it.copy(errorMessage = "Format NIP/ID Guru harus diawali kode institusi '${policy.approvedNipPrefix}' sesuai ketentuan Admin.")
                }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val newUser = User(
                    nama = state.nama.trim(),
                    nis = state.nis.trim(),
                    kelas = if (state.role == UserRole.STUDENT) state.kelas.trim() else "GURU / ADMIN",
                    kelompok = if (state.role == UserRole.TEACHER) {
                        policy.schoolName
                    } else if (state.mode == UserMode.KELOMPOK) {
                        state.kelompok.trim()
                    } else {
                        "INDIVIDU"
                    },
                    mode = state.mode,
                    role = state.role,
                    createdTimestamp = System.currentTimeMillis()
                )

                userRepository.registerOrLoginUser(newUser)
                _uiState.update { it.copy(isLoading = false, isSuccessRegister = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal menyimpan identitas pengguna"
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                IdentityViewModel(
                    userRepository = RepositoryModule.provideUserRepository(app),
                    adminPolicyRepository = RepositoryModule.provideAdminPolicyRepository(app)
                )
            }
        }
    }
}
