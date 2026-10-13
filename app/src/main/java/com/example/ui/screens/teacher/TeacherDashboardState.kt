package com.example.ui.screens.teacher

import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.model.ClassAnalyticsSummary
import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent
import com.example.domain.model.StudentLearningStatus
import com.example.domain.model.StudentSummary
import com.example.domain.model.User

enum class StudentSortOption {
    NAME_ASC,
    SCORE_DESC,
    PROGRESS_DESC
}

enum class AdminDashboardSection(val title: String) {
    OVERVIEW_CLASS("Analitik & Siswa"),
    CMS_CONTENT("Kelola Konten / CMS"),
    ROLE_ACCESS_POLICY("Pengaturan Peran"),
    MODULE_CURRICULUM("Konten & KKM"),
    USER_ROLE_MANAGEMENT("Kelola Akun")
}

data class TeacherDashboardState(
    val selectedSection: AdminDashboardSection = AdminDashboardSection.OVERVIEW_CLASS,
    val searchQuery: String = "",
    val selectedFilter: StudentLearningStatus? = null,
    val sortOption: StudentSortOption = StudentSortOption.NAME_ASC,
    val classSummary: ClassAnalyticsSummary = ClassAnalyticsSummary(),
    val allStudents: List<StudentSummary> = emptyList(),
    val filteredStudents: List<StudentSummary> = emptyList(),
    val allRegisteredUsers: List<User> = emptyList(),
    val selectedStudentDetail: StudentSummary? = null,
    val policy: AdminRoleAccessPolicy = AdminRoleAccessPolicy(),
    
    // CMS Dynamic Content States
    val cmsContents: List<DynamicContent> = emptyList(),
    val cmsFilteredContents: List<DynamicContent> = emptyList(),
    val cmsSelectedCategory: ContentCategory? = null,
    val cmsSearchQuery: String = "",
    val isContentEditorOpen: Boolean = false,
    val editingContent: DynamicContent? = null,
    val contentToDelete: DynamicContent? = null,
    val isResetDefaultConfirmOpen: Boolean = false,
    val prefilledCategoryForNew: ContentCategory? = null,

    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
