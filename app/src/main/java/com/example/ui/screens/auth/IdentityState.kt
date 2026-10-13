package com.example.ui.screens.auth

import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.model.User
import com.example.domain.model.UserMode
import com.example.domain.model.UserRole

data class IdentityState(
    val nama: String = "",
    val nis: String = "",
    val kelas: String = "X-1",
    val kelompok: String = "",
    val mode: UserMode = UserMode.INDIVIDU,
    val role: UserRole = UserRole.STUDENT,
    val adminPinInput: String = "",
    val classAccessCodeInput: String = "",
    val policy: AdminRoleAccessPolicy = AdminRoleAccessPolicy(),
    val existingUsers: List<User> = emptyList(),
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccessRegister: Boolean = false
)
