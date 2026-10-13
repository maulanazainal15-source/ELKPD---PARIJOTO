package com.example.ui.screens.teacher

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.model.BibliographyCategory
import com.example.domain.model.BibliographyItem
import com.example.domain.model.ClassAnalyticsSummary
import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent
import com.example.domain.model.StudentLearningStatus
import com.example.domain.model.StudentSubmission
import com.example.domain.model.StudentSummary
import com.example.domain.model.SubmissionType
import com.example.domain.model.User
import com.example.domain.model.UserMode
import com.example.domain.model.UserRole
import com.example.domain.repository.AdminPolicyRepository
import com.example.domain.repository.BibliographyRepository
import com.example.domain.repository.DynamicContentRepository
import com.example.domain.repository.EvaluationRepository
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.StudentSubmissionRepository
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
    private val dynamicContentRepository: DynamicContentRepository? = null,
    private val studentSubmissionRepository: StudentSubmissionRepository? = null,
    private val bibliographyRepository: BibliographyRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherDashboardState())
    val uiState: StateFlow<TeacherDashboardState> = _uiState.asStateFlow()

    init {
        loadClassData()
        observeCmsContents()
        observeSubmissions()
        observeBibliographies()
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
                    val needsRemedial = summaries.count { it.status == StudentLearningStatus.NEEDS_REMEDIAL }
                    val avgScore = if (total > 0) summaries.map { it.hotsScore }.average() else 0.0
                    val highest = summaries.maxOfOrNull { it.hotsScore } ?: 0.0
                    val lowest = summaries.minOfOrNull { it.hotsScore } ?: 0.0

                    val avgProgress = if (total > 0) summaries.map { it.overallPercentage }.average() else 0.0
                    val summary = ClassAnalyticsSummary(
                        totalStudents = total,
                        averageHotsScore = avgScore,
                        averageProgressPercentage = avgProgress,
                        completedCount = completed,
                        inProgressCount = inProgress,
                        remedialCount = needsRemedial
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

    private fun observeSubmissions() {
        val repo = studentSubmissionRepository ?: return
        viewModelScope.launch {
            repo.getAllSubmissions().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        submissions = list,
                        filteredSubmissions = applySubmissionsFilter(list, state.submissionSearchQuery, state.submissionFilterType)
                    )
                }
            }
        }
    }

    private fun observeBibliographies() {
        val repo = bibliographyRepository ?: return
        viewModelScope.launch {
            repo.getAllBibliographies().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        bibliographies = list,
                        filteredBibliographies = applyBibliographyFilter(list, state.bibliographySearchQuery, state.bibliographyCategoryFilter)
                    )
                }
            }
        }
    }

    // --- SUBMISSIONS VIEWER ACTIONS ---
    fun onSubmissionFilterSelect(type: SubmissionType?) {
        _uiState.update { state ->
            val newType = if (state.submissionFilterType == type) null else type
            state.copy(
                submissionFilterType = newType,
                filteredSubmissions = applySubmissionsFilter(state.submissions, state.submissionSearchQuery, newType)
            )
        }
    }

    fun onSubmissionSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                submissionSearchQuery = query,
                filteredSubmissions = applySubmissionsFilter(state.submissions, query, state.submissionFilterType)
            )
        }
    }

    fun openSubmissionDetail(submission: StudentSubmission) {
        _uiState.update { it.copy(inspectingSubmission = submission) }
    }

    fun closeSubmissionDetail() {
        _uiState.update { it.copy(inspectingSubmission = null) }
    }

    fun updateSubmissionStatus(submissionId: String, newStatus: String) {
        viewModelScope.launch {
            studentSubmissionRepository?.updateSubmissionStatus(submissionId, newStatus)
            _uiState.update {
                it.copy(
                    successMessage = "Status submisi berhasil diubah menjadi: $newStatus",
                    inspectingSubmission = it.inspectingSubmission?.copy(status = newStatus)
                )
            }
        }
    }

    fun confirmDeleteSubmission(submission: StudentSubmission) {
        _uiState.update { it.copy(submissionToDelete = submission) }
    }

    fun dismissDeleteSubmission() {
        _uiState.update { it.copy(submissionToDelete = null) }
    }

    fun executeDeleteSubmission() {
        val sub = _uiState.value.submissionToDelete ?: return
        viewModelScope.launch {
            studentSubmissionRepository?.deleteSubmission(sub.id)
            _uiState.update {
                it.copy(
                    submissionToDelete = null,
                    inspectingSubmission = null,
                    successMessage = "Submisi jawaban siswa berhasil dihapus."
                )
            }
        }
    }

    private fun applySubmissionsFilter(
        items: List<StudentSubmission>,
        query: String,
        type: SubmissionType?
    ): List<StudentSubmission> {
        val q = query.trim().lowercase()
        return items.filter { sub ->
            val matchesType = type == null || sub.submissionType == type
            val matchesQuery = q.isEmpty() ||
                sub.studentName.lowercase().contains(q) ||
                sub.studentNis.lowercase().contains(q) ||
                sub.studentClass.lowercase().contains(q) ||
                sub.title.lowercase().contains(q)
            matchesType && matchesQuery
        }
    }

    // --- BIBLIOGRAPHY CRUD ACTIONS ---
    fun onBibliographyCategoryFilterSelect(category: BibliographyCategory?) {
        _uiState.update { state ->
            val newCat = if (state.bibliographyCategoryFilter == category) null else category
            state.copy(
                bibliographyCategoryFilter = newCat,
                filteredBibliographies = applyBibliographyFilter(state.bibliographies, state.bibliographySearchQuery, newCat)
            )
        }
    }

    fun onBibliographySearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                bibliographySearchQuery = query,
                filteredBibliographies = applyBibliographyFilter(state.bibliographies, query, state.bibliographyCategoryFilter)
            )
        }
    }

    fun openAddBibliographyDialog() {
        _uiState.update {
            it.copy(
                isBibliographyEditorOpen = true,
                editingBibliography = null
            )
        }
    }

    fun openEditBibliographyDialog(item: BibliographyItem) {
        _uiState.update {
            it.copy(
                isBibliographyEditorOpen = true,
                editingBibliography = item
            )
        }
    }

    fun closeBibliographyDialog() {
        _uiState.update {
            it.copy(
                isBibliographyEditorOpen = false,
                editingBibliography = null
            )
        }
    }

    fun saveBibliographyItem(
        title: String,
        authors: String,
        year: String,
        source: String,
        category: BibliographyCategory,
        doiOrUrl: String,
        description: String
    ) {
        if (title.isBlank() || authors.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Judul dan Penulis referensi wajib diisi!") }
            return
        }

        viewModelScope.launch {
            val editing = _uiState.value.editingBibliography
            val item = editing?.copy(
                title = title.trim(),
                authors = authors.trim(),
                year = year.trim().ifBlank { "2024" },
                source = source.trim(),
                category = category,
                doiOrUrl = doiOrUrl.trim(),
                description = description.trim(),
                updatedAt = System.currentTimeMillis()
            ) ?: BibliographyItem(
                id = "ref_${UUID.randomUUID().toString().take(8)}",
                title = title.trim(),
                authors = authors.trim(),
                year = year.trim().ifBlank { "2024" },
                source = source.trim(),
                category = category,
                doiOrUrl = doiOrUrl.trim(),
                description = description.trim(),
                orderIndex = (_uiState.value.bibliographies.maxOfOrNull { it.orderIndex } ?: 0) + 1,
                updatedAt = System.currentTimeMillis()
            )

            bibliographyRepository?.saveBibliography(item)
            _uiState.update {
                it.copy(
                    isBibliographyEditorOpen = false,
                    editingBibliography = null,
                    successMessage = "Referensi pustaka berhasil disimpan dan tersinkron ke siswa."
                )
            }
        }
    }

    fun confirmDeleteBibliography(item: BibliographyItem) {
        _uiState.update { it.copy(bibliographyToDelete = item) }
    }

    fun dismissDeleteBibliography() {
        _uiState.update { it.copy(bibliographyToDelete = null) }
    }

    fun executeDeleteBibliography() {
        val item = _uiState.value.bibliographyToDelete ?: return
        viewModelScope.launch {
            bibliographyRepository?.deleteBibliography(item.id)
            _uiState.update {
                it.copy(
                    bibliographyToDelete = null,
                    successMessage = "Referensi pustaka berhasil dihapus."
                )
            }
        }
    }

    private fun applyBibliographyFilter(
        items: List<BibliographyItem>,
        query: String,
        category: BibliographyCategory?
    ): List<BibliographyItem> {
        val q = query.trim().lowercase()
        return items.filter { item ->
            val matchesCategory = category == null || item.category == category
            val matchesQuery = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.authors.lowercase().contains(q) ||
                item.source.lowercase().contains(q) ||
                item.year.contains(q)
            matchesCategory && matchesQuery
        }
    }

    // --- SECURITY PIN MANAGEMENT ACTIONS ---
    fun openChangePinDialog() {
        _uiState.update { it.copy(isChangePinDialogOpen = true) }
    }

    fun closeChangePinDialog() {
        _uiState.update { it.copy(isChangePinDialogOpen = false) }
    }

    fun executeChangeSecurityPin(oldPin: String, newPin: String, confirmPin: String): Boolean {
        val currentPin = _uiState.value.policy.adminPin
        if (oldPin.trim() != currentPin.trim()) {
            _uiState.update { it.copy(errorMessage = "PIN Lama tidak sesuai!") }
            return false
        }
        if (newPin.trim().length < 4) {
            _uiState.update { it.copy(errorMessage = "PIN Baru minimal 4 karakter digit!") }
            return false
        }
        if (newPin.trim() != confirmPin.trim()) {
            _uiState.update { it.copy(errorMessage = "Konfirmasi PIN Baru tidak cocok!") }
            return false
        }

        viewModelScope.launch {
            val updatedPolicy = _uiState.value.policy.copy(adminPin = newPin.trim())
            adminPolicyRepository?.savePolicy(updatedPolicy)
            _uiState.update {
                it.copy(
                    policy = updatedPolicy,
                    isChangePinDialogOpen = false,
                    successMessage = "PIN Keamanan Guru berhasil diperbarui."
                )
            }
        }
        return true
    }

    // --- NAVIGATION SECTIONS ---
    fun onSectionSelect(section: AdminDashboardSection) {
        _uiState.update { it.copy(selectedSection = section) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilterAndSort()
    }

    fun onFilterSelect(status: StudentLearningStatus?) {
        _uiState.update {
            it.copy(selectedFilter = if (it.selectedFilter == status) null else status)
        }
        applyFilterAndSort()
    }

    fun onSortSelect(option: StudentSortOption) {
        _uiState.update { it.copy(sortOption = option) }
        applyFilterAndSort()
    }

    fun selectStudentDetail(student: StudentSummary?) {
        _uiState.update { it.copy(selectedStudentDetail = student) }
    }

    fun saveSchoolName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(schoolName = name.trim())
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update { it.copy(policy = updated, successMessage = "Identitas sekolah berhasil diperbarui dan tersinkron.") }
        }
    }

    fun saveAcademicYear(year: String) {
        if (year.isBlank()) return
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(academicYear = year.trim())
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update { it.copy(policy = updated, successMessage = "Tahun ajaran berhasil disimpan.") }
        }
    }

    fun updateMinimumPassingGrade(kkm: Int) {
        val coerced = kkm.coerceIn(50, 95)
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(minimumPassingGradeKkm = coerced)
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update { it.copy(policy = updated, successMessage = "Standar KKM diperbarui menjadi $coerced") }
            loadClassData()
        }
    }

    fun toggleRequireAdminPin(require: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(requireAdminPinForLogin = require)
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update { it.copy(policy = updated) }
        }
    }

    fun toggleEvaluationOpen(isOpen: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(isEvaluationOpen = isOpen)
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update {
                it.copy(
                    policy = updated,
                    successMessage = if (isOpen) "Evaluasi HOTS dibuka untuk siswa" else "Evaluasi HOTS dikunci"
                )
            }
        }
    }

    fun toggleAiTutorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(isAiTutorEnabled = enabled)
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update {
                it.copy(
                    policy = updated,
                    successMessage = if (enabled) "AI Tutor diaktifkan untuk siswa" else "AI Tutor dinonaktifkan"
                )
            }
        }
    }

    fun updateAllowedClasses(classes: String) {
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(allowedStudentClasses = classes)
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update { it.copy(policy = updated, successMessage = "Daftar kelas diizinkan diperbarui") }
        }
    }

    fun toggleIndividuMode(allow: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(allowIndividuMode = allow)
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update { it.copy(policy = updated) }
        }
    }

    fun toggleKelompokMode(allow: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(allowKelompokMode = allow)
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update { it.copy(policy = updated) }
        }
    }

    fun updateAnnouncement(announcement: String) {
        viewModelScope.launch {
            val updated = _uiState.value.policy.copy(announcementBanner = announcement)
            adminPolicyRepository?.savePolicy(updated)
            _uiState.update { it.copy(policy = updated, successMessage = "Pengumuman berhasil disinkronkan ke siswa") }
        }
    }

    fun registerNewUserByAdmin(
        nama: String,
        nis: String,
        kelas: String,
        kelompok: String,
        mode: UserMode,
        role: UserRole
    ) {
        viewModelScope.launch {
            if (nama.isBlank() || nis.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Nama dan NIS/NIP tidak boleh kosong") }
                return@launch
            }

            try {
                userRepository.insertOrUpdateStudent(
                    User(
                        nama = nama.trim(),
                        nis = nis.trim(),
                        kelas = kelas.trim(),
                        kelompok = kelompok.trim(),
                        mode = mode,
                        role = role
                    )
                )
                _uiState.update { it.copy(successMessage = "Akun ${role.name} $nama berhasil didaftarkan") }
                loadClassData()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Gagal mendaftarkan akun: ${e.message}") }
            }
        }
    }

    fun deleteUserByAdmin(user: User) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(user.id)
                _uiState.update { it.copy(successMessage = "Pengguna ${user.nama} berhasil dihapus") }
                loadClassData()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Gagal menghapus pengguna: ${e.message}") }
            }
        }
    }

    // --- CMS CRUD METHODS ---
    private fun observeCmsContents() {
        val repo = dynamicContentRepository ?: return
        viewModelScope.launch {
            repo.getAllContents().collect { contents ->
                _uiState.update { state ->
                    state.copy(
                        cmsContents = contents,
                        cmsFilteredContents = contents
                    )
                }
                filterCmsContents()
            }
        }
    }

    fun onCmsCategorySelect(category: ContentCategory?) {
        _uiState.update {
            it.copy(cmsSelectedCategory = if (it.cmsSelectedCategory == category) null else category)
        }
        filterCmsContents()
    }

    fun onCmsSearchQueryChange(query: String) {
        _uiState.update { it.copy(cmsSearchQuery = query) }
        filterCmsContents()
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
                prefilledCategoryForNew = content.category
            )
        }
    }

    fun closeContentEditorDialog() {
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
        content: String,
        category: ContentCategory,
        subCategory: String,
        orderIndex: Int,
        author: String
    ) {
        if (title.isBlank() || content.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Judul dan Isi konten materi tidak boleh kosong!") }
            return
        }

        viewModelScope.launch {
            val repo = dynamicContentRepository ?: return@launch
            val now = System.currentTimeMillis()

            val contentToSave = if (id != null) {
                val existing = repo.getContentById(id)
                existing?.copy(
                    category = category,
                    subCategory = subCategory.ifBlank { category.badge },
                    title = title.trim(),
                    content = content.trim(),
                    orderIndex = orderIndex,
                    author = author.ifBlank { "Guru / Administrator" },
                    lastUpdated = now
                ) ?: DynamicContent(
                    id = id,
                    category = category,
                    subCategory = subCategory.ifBlank { category.badge },
                    title = title.trim(),
                    content = content.trim(),
                    orderIndex = orderIndex,
                    author = author.ifBlank { "Guru / Administrator" },
                    lastUpdated = now
                )
            } else {
                DynamicContent(
                    id = "cms_${UUID.randomUUID().toString().take(8)}",
                    category = category,
                    subCategory = subCategory.ifBlank { category.badge },
                    title = title.trim(),
                    content = content.trim(),
                    orderIndex = orderIndex,
                    author = author.ifBlank { "Guru / Administrator" },
                    lastUpdated = now
                )
            }

            repo.saveContent(contentToSave)
            _uiState.update {
                it.copy(
                    isContentEditorOpen = false,
                    editingContent = null,
                    prefilledCategoryForNew = null,
                    successMessage = "Konten materi '${contentToSave.title}' berhasil disimpan dan tersinkron!"
                )
            }
        }
    }

    fun saveContent(
        category: ContentCategory,
        subCategory: String,
        title: String,
        contentBody: String,
        orderIndex: Int
    ) {
        val editing = _uiState.value.editingContent
        saveContent(
            id = editing?.id,
            title = title,
            content = contentBody,
            category = category,
            subCategory = subCategory,
            orderIndex = orderIndex,
            author = editing?.author ?: "Guru / Administrator"
        )
    }

    fun confirmDeleteContent(content: DynamicContent) {
        _uiState.update { it.copy(contentToDelete = content) }
    }

    fun dismissDeleteContent() {
        _uiState.update { it.copy(contentToDelete = null) }
    }

    fun executeDeleteContent() {
        val content = _uiState.value.contentToDelete ?: return
        viewModelScope.launch {
            val repo = dynamicContentRepository ?: return@launch
            repo.deleteContent(content.id)
            _uiState.update {
                it.copy(
                    contentToDelete = null,
                    successMessage = "Konten '${content.title}' berhasil dihapus."
                )
            }
        }
    }

    fun openResetDefaultConfirmDialog() {
        _uiState.update { it.copy(isResetDefaultConfirmOpen = true) }
    }

    fun dismissResetDefaultConfirmDialog() {
        _uiState.update { it.copy(isResetDefaultConfirmOpen = false) }
    }

    fun executeResetToDefaultContents() {
        viewModelScope.launch {
            val repo = dynamicContentRepository ?: return@launch
            repo.resetToDefault()
            _uiState.update {
                it.copy(
                    isResetDefaultConfirmOpen = false,
                    successMessage = "Seluruh konten dikembalikan ke kurikulum standar etnosains Parijoto."
                )
            }
        }
    }

    // Helper aliases for UI calls
    fun updatePolicyDraft(updated: AdminRoleAccessPolicy) {
        _uiState.update { it.copy(policy = updated) }
    }

    fun saveAdminPolicy() {
        viewModelScope.launch {
            adminPolicyRepository?.savePolicy(_uiState.value.policy)
            _uiState.update { it.copy(successMessage = "Pengaturan kebijakan & PIN berhasil disimpan.") }
        }
    }

    fun resetPolicyToDefault() {
        viewModelScope.launch {
            val def = AdminRoleAccessPolicy()
            adminPolicyRepository?.savePolicy(def)
            _uiState.update { it.copy(policy = def, successMessage = "Kebijakan dikembalikan ke default.") }
        }
    }

    fun toggleUserRole(user: User) {
        viewModelScope.launch {
            val newRole = if (user.role == UserRole.TEACHER) UserRole.STUDENT else UserRole.TEACHER
            val updated = user.copy(role = newRole)
            userRepository.insertOrUpdateStudent(updated)
            loadClassData()
        }
    }

    fun deleteRegisteredUser(userId: Int) {
        deleteUserById(userId)
    }

    fun deleteRegisteredUser(user: User) {
        deleteUserById(user.id)
    }

    fun deleteUserById(userId: Int) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(userId)
                _uiState.update { it.copy(successMessage = "Pengguna berhasil dihapus") }
                loadClassData()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Gagal menghapus pengguna: ${e.message}") }
            }
        }
    }

    fun createSampleOrNewUser(nama: String, nis: String, kelas: String, role: UserRole) {
        registerNewUserByAdmin(nama, nis, kelas, "", UserMode.INDIVIDU, role)
    }

    fun openResetDefaultConfirm() = openResetDefaultConfirmDialog()
    fun dismissResetDefaultConfirm() = dismissResetDefaultConfirmDialog()
    fun resetContentsToDefault() = executeResetToDefaultContents()
    fun closeContentEditor() = closeContentEditorDialog()
    fun deleteContent(id: String) {
        viewModelScope.launch {
            dynamicContentRepository?.deleteContent(id)
            _uiState.update { it.copy(contentToDelete = null, successMessage = "Konten berhasil dihapus.") }
        }
    }

    private fun filterCmsContents() {
        val query = _uiState.value.cmsSearchQuery.trim().lowercase()
        val cat = _uiState.value.cmsSelectedCategory

        val filtered = _uiState.value.cmsContents.filter { content ->
            val matchesCategory = cat == null || content.category == cat
            val matchesQuery = query.isEmpty() ||
                content.title.lowercase().contains(query) ||
                content.content.lowercase().contains(query) ||
                content.subCategory.lowercase().contains(query) ||
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
                    dynamicContentRepository = RepositoryModule.provideDynamicContentRepository(app),
                    studentSubmissionRepository = RepositoryModule.provideStudentSubmissionRepository(app),
                    bibliographyRepository = RepositoryModule.provideBibliographyRepository(app)
                )
            }
        }
    }
}
