package com.example.ui.screens.profile

import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.model.User

data class StudentAnalytics(
    val act1Percentage: Int = 0,
    val act2Percentage: Int = 0,
    val overallLearningPercentage: Int = 0,
    val hotsPgScore: Double = 0.0,
    val isHotsSubmitted: Boolean = false,
    val completedActivitiesCount: Int = 0,
    val totalActivitiesCount: Int = 2
)

data class ProfileState(
    val user: User? = null,
    val analytics: StudentAnalytics = StudentAnalytics(),
    val policy: AdminRoleAccessPolicy = AdminRoleAccessPolicy(),
    val isEditDialogVisible: Boolean = false,
    val isResetDialogVisible: Boolean = false,
    val isAdminPinDialogVisible: Boolean = false,
    val editedName: String = "",
    val editedSchool: String = "",
    val editedClass: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

