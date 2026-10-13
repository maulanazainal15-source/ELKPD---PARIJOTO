package com.example.ui.screens.teacher

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.model.ClassAnalyticsSummary
import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent
import com.example.domain.model.StudentLearningStatus
import com.example.domain.model.StudentSummary
import com.example.domain.model.User
import com.example.domain.model.UserMode
import com.example.domain.model.UserRole
import com.example.domain.repository.AdminPolicyRepository
import com.example.domain.repository.DynamicContentRepository
import com.example.domain.repository.EvaluationRepository
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class TeacherDashboardViewModel(
    private val userRepository: UserRepository,
    private val learningRepository: LearningRepository,
    private val evaluationRepository: EvaluationRepository,
    private val adminPolicyRepository: AdminPolicyRepository? = null,
    private val dynamicContentRepository: DynamicContentRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherDashboardState())
    val uiState: StateFlow<TeacherDashboardState> = _uiState.asStateFlow()

    init {
        loadClassData()
        observeCmsContents()
    }

    fun loadClassData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val policyFlow = adminPolicyRepository?.observePolicy() ?: flowOf(AdminRoleAccessPolicy())

            combine(
                userRepository.getAllStudents(),
                userRepository.getAllUsers(),
                policyFlow
            ) { students, allUsers, policy ->
                Triple(students, allUsers, policy)
            }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { (students, allUsers, policy) ->
                    val kkm = policy.minimumPassingGradeKkm.toDouble()
                    val summaries = students.map { user ->
                        val act1 = learningRepository.getProgressByUserAndActivity(user.id, "ACT_1").firstOrNull()?.percentage ?: 0
                        val act2 = learningRepository.getProgressByUserAndActivity(user.id, "ACT_2").firstOrNull()?.percentage ?: 0
                        val eval = evaluationRepository.getResultByUser(user.id).firstOrNull()

                        val overall = (act1 + act2) / 2
                        val score = eval?.pgScore ?: 0.0
                        val isSubmitted = eval?.isSubmitted ?: false

                        val status = when {
                            isSubmitted && score < kkm -> StudentLearningStatus.NEEDS_REMEDIAL
                            overall < 60 -> StudentLearningStatus.NEEDS_REMEDIAL
                            overall >= 100 && isSubmitted -> StudentLearningStatus.COMPLETED
                            else -> StudentLearningStatus.IN_PROGRESS
                        }

                        StudentSummary(
                            studentId = user.id,
                            fullName = user.nama,
                            schoolName = policy.schoolName,
                            className = user.kelas,
                            nisn = user.nis,
                            act1Percentage = act1,
                            act2Percentage = act2,
                            overallPercentage = overall,
                            hotsScore = score,
                            isHotsSubmitted = isSubmitted,
                            status = status
                        )
                    }

                    val total = summaries.size
                    val completed = summaries.count { it.status == StudentLearningStatus.COMPLETED }
                    val inProgress = summaries.count { it.status == StudentLearningStatus.IN_PROGRESS }
                    val remedial = summaries.count { it.status == StudentLearningStatus.NEEDS_REMEDIAL }
                    val avgScore = if (total > 0) summaries.map { it.hotsScore }.average() else 0.0
                    val avgProgress = if (total > 0) summaries.map { it.overallPercentage.toDouble() }.average() else 0.0

                    val summary = ClassAnalyticsSummary(
                        totalStudents = total,
                        averageHotsScore = avgScore,
                        averageProgressPercentage = avgProgress,
                        completedCount = completed,
                        inProgressCount = inProgress,
                        remedialCount = remedial
                    )

                    _uiState.update {
                        it.copy(
                            classSummary = summary,
                            allStudents = summaries,
                            allRegisteredUsers = allUsers,
                            policy = policy,
                            isLoading = false
                        )
                    }
                    applyFilterAndSort()
                }
        }
    }

    private fun observeCmsContents() {
        viewModelScope.launch {
            dynamicContentRepository?.getAllContents()
                ?.catch { e ->
                    _uiState.update { it.copy(errorMessage = "Gagal memuat konten CMS: ${e.message}") }
                }
                ?.collect { contents ->
                    _uiState.update {
                        it.copy(cmsContents = contents)
                    }
                    applyCmsFilter()
                }
        }
    }

    fun onSectionSelect(section: AdminDashboardSection) {
        _uiState.update { it.copy(selectedSection = section) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilterAndSort()
    }

    fun onFilterSelect(filter: StudentLearningStatus?) {
        _uiState.update { it.copy(selectedFilter = filter) }
        applyFilterAndSort()
    }

    fun onSortSelect(sort: StudentSortOption) {
        _uiState.update { it.copy(sortOption = sort) }
        applyFilterAndSort()
    }

    fun selectStudentDetail(student: StudentSummary?) {
        _uiState.update { it.copy(selectedStudentDetail = student) }
    }

    fun updatePolicyDraft(newPolicy: AdminRoleAccessPolicy) {
        _uiState.update { it.copy(policy = newPolicy) }
    }

    fun saveAdminPolicy() {
        val repo = adminPolicyRepository ?: return
        viewModelScope.launch {
            try {
                repo.savePolicy(_uiState.value.policy)
                _uiState.update {
                    it.copy(successMessage = "Ketentuan akses peran berhasil disimpan!")
                }
                loadClassData()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Gagal menyimpan ketentuan: ${e.message}")
                }
            }
        }
    }

    fun resetPolicyToDefault() {
        val repo = adminPolicyRepository ?: return
        viewModelScope.launch {
            try {
                val defaultPolicy = repo.resetToDefaultPolicy()
                _uiState.update {
                    it.copy(
                        policy = defaultPolicy,
                        successMessage = "Pengaturan dikembalikan ke standar awal."
                    )
                }
                loadClassData()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Gagal mereset pengaturan: ${e.message}")
                }
            }
        }
    }

    fun toggleUserRole(user: User) {
        val newRole = if (user.role == UserRole.TEACHER) UserRole.STUDENT else UserRole.TEACHER
        viewModelScope.launch {
            val updated = user.copy(
                role = newRole,
                kelas = if (newRole == UserRole.TEACHER) "GURU / ADMIN" else "X-1"
            )
            userRepository.insertOrUpdateStudent(updated)
            _uiState.update {
                it.copy(
                    successMessage = "Peran ${user.nama} berhasil diubah menjadi ${if (newRole == UserRole.TEACHER) "Guru / Administrator" else "Siswa"}."
                )
            }
        }
    }

    fun createSampleOrNewUser(nama: String, nis: String, kelas: String, role: UserRole) {
        if (nama.isBlank() || nis.isBlank()) return
        viewModelScope.launch {
            val newUser = User(
                nama = nama.trim(),
                nis = nis.trim(),
                kelas = if (role == UserRole.TEACHER) "GURU / ADMIN" else kelas.trim().ifBlank { "X-1" },
                kelompok = _uiState.value.policy.schoolName,
                mode = UserMode.INDIVIDU,
                role = role
            )
            userRepository.registerOrLoginUser(newUser)
            _uiState.update {
                it.copy(
                    successMessage = "Akun ${newUser.nama} (${if (role == UserRole.TEACHER) "Guru/Admin" else "Siswa"}) berhasil ditambahkan."
                )
            }
        }
    }

    fun deleteRegisteredUser(userId: Int) {
        viewModelScope.launch {
            userRepository.deleteUser(userId)
            _uiState.update {
                it.copy(successMessage = "Akun pengguna berhasil dihapus.")
            }
        }
    }

    // ==========================================
    // CMS DYNAMIC CONTENT CRUD METHODS
    // ==========================================

    fun onCmsCategorySelect(category: ContentCategory?) {
        _uiState.update { it.copy(cmsSelectedCategory = category) }
        applyCmsFilter()
    }

    fun onCmsSearchQueryChange(query: String) {
        _uiState.update { it.copy(cmsSearchQuery = query) }
        applyCmsFilter()
    }

    fun openAddContentDialog(prefilledCategory: ContentCategory? = null) {
        _uiState.update {
            it.copy(
                isContentEditorOpen = true,
                editingContent = null,
                prefilledCategoryForNew = prefilledCategory
            )
        }
    }

    fun openEditContentDialog(content: DynamicContent) {
        _uiState.update {
            it.copy(
                isContentEditorOpen = true,
                editingContent = content,
                prefilledCategoryForNew = null
            )
        }
    }

    fun closeContentEditor() {
        _uiState.update {
            it.copy(
                isContentEditorOpen = false,
                editingContent = null,
                prefilledCategoryForNew = null
            )
        }
    }

    fun saveContent(
        id: String?,
        title: String,
        contentText: String,
        category: ContentCategory,
        subCategory: String,
        orderIndex: Int,
        author: String
    ) {
        val trimmedTitle = title.trim()
        val trimmedContent = contentText.trim()
        val trimmedAuthor = author.trim().ifBlank { "Guru / Administrator" }
        val trimmedSubCat = subCategory.trim().ifBlank { "UMUM" }

        if (trimmedTitle.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Judul konten wajib diisi dan tidak boleh hanya spasi!") }
            return
        }
        if (trimmedContent.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Teks isi materi/panduan tidak boleh kosong!") }
            return
        }

        viewModelScope.launch {
            val targetId = id ?: UUID.randomUUID().toString()
            val contentToSave = DynamicContent(
                id = targetId,
                category = category,
                subCategory = trimmedSubCat,
                title = trimmedTitle,
                content = trimmedContent,
                orderIndex = orderIndex,
                author = trimmedAuthor,
                lastUpdated = System.currentTimeMillis(),
                isDefault = false
            )

            val result = dynamicContentRepository?.saveContent(contentToSave)
            if (result?.isSuccess == true) {
                _uiState.update {
                    it.copy(
                        isContentEditorOpen = false,
                        editingContent = null,
                        successMessage = "Konten '$trimmedTitle' berhasil disimpan!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "Gagal menyimpan konten: ${result?.exceptionOrNull()?.message ?: "Terjadi kesalahan"}"
                    )
                }
            }
        }
    }

    fun confirmDeleteContent(content: DynamicContent) {
        _uiState.update { it.copy(contentToDelete = content) }
    }

    fun dismissDeleteContent() {
        _uiState.update { it.copy(contentToDelete = null) }
    }

    fun deleteContent(id: String) {
        viewModelScope.launch {
            val result = dynamicContentRepository?.deleteContent(id)
            if (result?.isSuccess == true) {
                _uiState.update {
                    it.copy(
                        contentToDelete = null,
                        successMessage = "Konten berhasil dihapus!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "Gagal menghapus konten: ${result?.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun openResetDefaultConfirm() {
        _uiState.update { it.copy(isResetDefaultConfirmOpen = true) }
    }

    fun dismissResetDefaultConfirm() {
        _uiState.update { it.copy(isResetDefaultConfirmOpen = false) }
    }

    fun resetContentsToDefault() {
        viewModelScope.launch {
            val result = dynamicContentRepository?.resetToDefault()
            if (result?.isSuccess == true) {
                _uiState.update {
                    it.copy(
                        isResetDefaultConfirmOpen = false,
                        successMessage = "Seluruh materi & panduan berhasil direset ke standar kurikulum!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "Gagal mereset konten: ${result?.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    private fun applyCmsFilter() {
        val query = _uiState.value.cmsSearchQuery.trim().lowercase()
        val selectedCategory = _uiState.value.cmsSelectedCategory

        val filtered = _uiState.value.cmsContents.filter { content ->
            val matchesCategory = selectedCategory == null || content.category == selectedCategory
            val matchesQuery = query.isEmpty() ||
                content.title.lowercase().contains(query) ||
                content.content.lowercase().contains(query) ||
                content.getSubCategoryLabel().lowercase().contains(query) ||
                content.author.lowercase().contains(query)
            matchesCategory && matchesQuery
        }

        _uiState.update { it.copy(cmsFilteredContents = filtered) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    private fun applyFilterAndSort() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val filter = _uiState.value.selectedFilter
        val sort = _uiState.value.sortOption

        var filtered = _uiState.value.allStudents.filter { student ->
            val matchesQuery = student.fullName.lowercase().contains(query) || student.nisn.contains(query)
            val matchesFilter = filter == null || student.status == filter
            matchesQuery && matchesFilter
        }

        filtered = when (sort) {
            StudentSortOption.NAME_ASC -> filtered.sortedBy { it.fullName }
            StudentSortOption.SCORE_DESC -> filtered.sortedByDescending { it.hotsScore }
            StudentSortOption.PROGRESS_DESC -> filtered.sortedByDescending { it.overallPercentage }
        }

        _uiState.update { it.copy(filteredStudents = filtered) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                TeacherDashboardViewModel(
                    userRepository = RepositoryModule.provideUserRepository(app),
                    learningRepository = RepositoryModule.provideLearningRepository(app),
                    evaluationRepository = RepositoryModule.provideEvaluationRepository(app),
                    adminPolicyRepository = RepositoryModule.provideAdminPolicyRepository(app),
                    dynamicContentRepository = RepositoryModule.provideDynamicContentRepository(app)
                )
            }
        }
    }
}
