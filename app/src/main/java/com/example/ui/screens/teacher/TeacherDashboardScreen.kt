package com.example.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.model.ClassAnalyticsSummary
import com.example.domain.model.StudentLearningStatus
import com.example.domain.model.StudentSummary
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.util.ClassReportExporter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    onBackClick: () -> Unit,
    onStudentClick: ((Int) -> Unit)? = null,
    onSwitchRoleClick: (() -> Unit)? = null,
    onOpenRubricConfigClick: (() -> Unit)? = null,
    viewModel: TeacherDashboardViewModel = viewModel(factory = TeacherDashboardViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddUserDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.successMessage, state.errorMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Dashboard Admin & Guru",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${state.policy.schoolName} • TA ${state.policy.academicYear}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (onSwitchRoleClick != null) {
                        IconButton(onClick = onSwitchRoleClick) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Ganti Peran Pengguna"
                            )
                        }
                    }
                    IconButton(onClick = {
                        ClassReportExporter.exportAndShareClassCsv(context, state.allStudents)
                    }) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Ekspor CSV/Excel")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Navigation Tabs untuk 4 Pilar Utama Dashboard Admin
            ScrollableTabRow(
                selectedTabIndex = state.selectedSection.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 12.dp
            ) {
                AdminDashboardSection.entries.forEach { section ->
                    val icon = when (section) {
                        AdminDashboardSection.OVERVIEW_CLASS -> Icons.Default.Analytics
                        AdminDashboardSection.CMS_CONTENT -> Icons.Default.MenuBook
                        AdminDashboardSection.ROLE_ACCESS_POLICY -> Icons.Default.Security
                        AdminDashboardSection.MODULE_CURRICULUM -> Icons.Default.Tune
                        AdminDashboardSection.USER_ROLE_MANAGEMENT -> Icons.Default.Group
                    }
                    Tab(
                        selected = state.selectedSection == section,
                        onClick = { viewModel.onSectionSelect(section) },
                        text = {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (state.selectedSection == section) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        icon = { Icon(imageVector = icon, contentDescription = section.title, modifier = Modifier.size(18.dp)) }
                    )
                }
            }

            when (state.selectedSection) {
                AdminDashboardSection.OVERVIEW_CLASS -> {
                    OverviewAndClassSection(
                        state = state,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onFilterSelect = viewModel::onFilterSelect,
                        onSortSelect = viewModel::onSortSelect,
                        onStudentClick = { student ->
                            viewModel.selectStudentDetail(student)
                            onStudentClick?.invoke(student.studentId)
                        },
                        onExportCsv = {
                            ClassReportExporter.exportAndShareClassCsv(context, state.allStudents)
                        },
                        onOpenRolePolicyTab = {
                            viewModel.onSectionSelect(AdminDashboardSection.ROLE_ACCESS_POLICY)
                        },
                        onOpenCmsTab = {
                            viewModel.onSectionSelect(AdminDashboardSection.CMS_CONTENT)
                        },
                        onOpenRubricConfig = onOpenRubricConfigClick
                    )
                }

                AdminDashboardSection.CMS_CONTENT -> {
                    CmsManagementSection(
                        state = state,
                        onCategorySelect = viewModel::onCmsCategorySelect,
                        onSearchQueryChange = viewModel::onCmsSearchQueryChange,
                        onAddContentClick = { cat ->
                            viewModel.openAddContentDialog(cat)
                        },
                        onEditContentClick = { content ->
                            viewModel.openEditContentDialog(content)
                        },
                        onDeleteContentClick = { content ->
                            viewModel.confirmDeleteContent(content)
                        },
                        onResetToDefaultClick = {
                            viewModel.openResetDefaultConfirm()
                        }
                    )
                }

                AdminDashboardSection.ROLE_ACCESS_POLICY -> {
                    RoleAccessPolicySection(
                        policy = state.policy,
                        onPolicyChange = viewModel::updatePolicyDraft,
                        onSavePolicy = viewModel::saveAdminPolicy,
                        onResetPolicy = viewModel::resetPolicyToDefault,
                        onSwitchRoleClick = onSwitchRoleClick
                    )
                }

                AdminDashboardSection.MODULE_CURRICULUM -> {
                    ModuleAndKkmConfigSection(
                        policy = state.policy,
                        onPolicyChange = viewModel::updatePolicyDraft,
                        onSavePolicy = viewModel::saveAdminPolicy,
                        onOpenRubricConfig = onOpenRubricConfigClick
                    )
                }

                AdminDashboardSection.USER_ROLE_MANAGEMENT -> {
                    UserRoleManagementSection(
                        users = state.allRegisteredUsers,
                        onToggleRole = viewModel::toggleUserRole,
                        onDeleteUser = viewModel::deleteRegisteredUser,
                        onAddUserClick = { showAddUserDialog = true },
                        onSwitchRoleClick = onSwitchRoleClick
                    )
                }
            }
        }
    }

    if (showAddUserDialog) {
        AddNewAccountDialog(
            allowedClasses = state.policy.getAllowedClassesList(),
            onDismiss = { showAddUserDialog = false },
            onConfirm = { nama, nis, kelas, role ->
                viewModel.createSampleOrNewUser(nama, nis, kelas, role)
                showAddUserDialog = false
            }
        )
    }

    // CMS Content Editor Dialog
    if (state.isContentEditorOpen) {
        ContentEditorDialog(
            initialContent = state.editingContent,
            defaultCategory = state.prefilledCategoryForNew,
            onDismiss = viewModel::closeContentEditor,
            onSave = { id, title, content, category, subCategory, orderIndex, author ->
                viewModel.saveContent(id, title, content, category, subCategory, orderIndex, author)
            }
        )
    }

    // CMS Delete Confirmation Dialog
    state.contentToDelete?.let { content ->
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteContent,
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Hapus Konten Ini?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Konten '${content.title}' (${content.category.badge}) akan dihapus secara permanen dari database aplikasi siswa.")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteContent(content.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteContent) {
                    Text("Batal")
                }
            }
        )
    }

    // CMS Reset Confirmation Dialog
    if (state.isResetDefaultConfirmOpen) {
        AlertDialog(
            onDismissRequest = viewModel::dismissResetDefaultConfirm,
            icon = { Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Reset ke Standar Kurikulum?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Tindakan ini akan mengembalikan seluruh materi, panduan penggunaan, K3, CP, TP, dan langkah praktikum ke standar bawaan kurikulum etnosains Parijoto.")
            },
            confirmButton = {
                Button(
                    onClick = viewModel::resetContentsToDefault,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Ya, Reset Konten")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissResetDefaultConfirm) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun OverviewAndClassSection(
    state: TeacherDashboardState,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelect: (StudentLearningStatus?) -> Unit,
    onSortSelect: (StudentSortOption) -> Unit,
    onStudentClick: (StudentSummary) -> Unit,
    onExportCsv: () -> Unit,
    onOpenRolePolicyTab: () -> Unit,
    onOpenCmsTab: () -> Unit,
    onOpenRubricConfig: (() -> Unit)?
) {
    val teacherCount = state.allRegisteredUsers.count { it.role == UserRole.TEACHER }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Executive Command Banner
        item {
            AdminCommandHeroCard(
                summary = state.classSummary,
                teacherCount = teacherCount,
                policy = state.policy,
                onOpenRolePolicyTab = onOpenRolePolicyTab,
                onOpenCmsTab = onOpenCmsTab,
                onOpenRubricConfig = onOpenRubricConfig
            )
        }

        // 2. Bento KPI Grid (4 Kartu Indikator Utama)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoKpiCard(
                    title = "Rata-rata HOTS",
                    value = String.format(Locale.getDefault(), "%.1f", state.classSummary.averageHotsScore),
                    subtitle = "Target KKM: ${state.policy.minimumPassingGradeKkm}",
                    icon = Icons.Default.Analytics,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                BentoKpiCard(
                    title = "Progres Modul",
                    value = "${state.classSummary.averageProgressPercentage.toInt()}%",
                    subtitle = "${state.classSummary.completedCount} Siswa Tuntas",
                    icon = Icons.Default.CheckCircle,
                    accentColor = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoKpiCard(
                    title = "Perlu Remedi",
                    value = "${state.classSummary.remedialCount} Siswa",
                    subtitle = "Di bawah KKM ${state.policy.minimumPassingGradeKkm}",
                    icon = Icons.Default.Warning,
                    accentColor = Color(0xFFD32F2F),
                    modifier = Modifier.weight(1f)
                )
                BentoKpiCard(
                    title = "Total Akun Aktif",
                    value = "${state.allRegisteredUsers.size} Akun",
                    subtitle = "${state.allStudents.size} Siswa • $teacherCount Guru",
                    icon = Icons.Default.Group,
                    accentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Pencarian, Filter & Urutan Siswa
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Capaian Peserta Didik",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = onExportCsv) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Unduh CSV")
                        }
                    }

                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari nama siswa atau NISN...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.selectedFilter == null,
                            onClick = { onFilterSelect(null) },
                            label = { Text("Semua (${state.allStudents.size})") }
                        )
                        FilterChip(
                            selected = state.selectedFilter == StudentLearningStatus.NEEDS_REMEDIAL,
                            onClick = { onFilterSelect(StudentLearningStatus.NEEDS_REMEDIAL) },
                            label = { Text("Remedi (${state.classSummary.remedialCount})") }
                        )
                        FilterChip(
                            selected = state.selectedFilter == StudentLearningStatus.COMPLETED,
                            onClick = { onFilterSelect(StudentLearningStatus.COMPLETED) },
                            label = { Text("Tuntas (${state.classSummary.completedCount})") }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Urutkan:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FilterChip(
                            selected = state.sortOption == StudentSortOption.NAME_ASC,
                            onClick = { onSortSelect(StudentSortOption.NAME_ASC) },
                            label = { Text("Nama A-Z") }
                        )
                        FilterChip(
                            selected = state.sortOption == StudentSortOption.SCORE_DESC,
                            onClick = { onSortSelect(StudentSortOption.SCORE_DESC) },
                            label = { Text("Skor HOTS") }
                        )
                        FilterChip(
                            selected = state.sortOption == StudentSortOption.PROGRESS_DESC,
                            onClick = { onSortSelect(StudentSortOption.PROGRESS_DESC) },
                            label = { Text("Progres") }
                        )
                    }
                }
            }
        }

        if (state.filteredStudents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum Ada Data Siswa pada Filter Ini",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Anda dapat menambahkan akun siswa melalui tab 'Kelola Akun' atau meminta siswa masuk dari layar Identitas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(state.filteredStudents, key = { it.studentId }) { student ->
                StudentItemCard(
                    student = student,
                    kkm = state.policy.minimumPassingGradeKkm,
                    onClick = { onStudentClick(student) }
                )
            }
        }
    }
}

@Composable
private fun AdminCommandHeroCard(
    summary: ClassAnalyticsSummary,
    teacherCount: Int,
    policy: AdminRoleAccessPolicy,
    onOpenRolePolicyTab: () -> Unit,
    onOpenCmsTab: () -> Unit,
    onOpenRubricConfig: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Pusat Kendali Etnosains Muria",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = "PIN Admin Aktif: ${policy.adminPin} • Kode Kelas: ${if (policy.requireClassAccessCode) policy.classAccessCode else "Terbuka"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Pengumuman Aktif: \"${policy.announcementBanner}\"",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.95f)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = onOpenCmsTab,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kelola Konten CMS", style = MaterialTheme.typography.labelSmall)
                    }
                    FilledTonalButton(
                        onClick = onOpenRolePolicyTab,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ketentuan Peran", style = MaterialTheme.typography.labelSmall)
                    }
                    if (onOpenRubricConfig != null) {
                        FilledTonalButton(
                            onClick = onOpenRubricConfig,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rubrik HOTS", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BentoKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RoleAccessPolicySection(
    policy: AdminRoleAccessPolicy,
    onPolicyChange: (AdminRoleAccessPolicy) -> Unit,
    onSavePolicy: () -> Unit,
    onResetPolicy: () -> Unit,
    onSwitchRoleClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Card 1: Ketentuan Masuk Peran Guru / Administrator
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Ketentuan Masuk Peran Guru / Administrator",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Atur syarat otorisasi ketika pengguna memilih peran Guru/Admin pada halaman masuk.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Wajibkan PIN Otorisasi Guru / Admin",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Mencegah siswa membuka Dashboard Admin tanpa izin",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = policy.requireAdminPinForLogin,
                        onCheckedChange = { onPolicyChange(policy.copy(requireAdminPinForLogin = it)) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = policy.adminPin,
                    onValueChange = { onPolicyChange(policy.copy(adminPin = it)) },
                    label = { Text("PIN Keamanan Guru / Administrator") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_policy_pin_input")
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Validasi Format Awalan NIP / ID Guru",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Hanya mengizinkan NIP yang diawali kode institusi tertentu",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = policy.requireApprovedNipPrefix,
                        onCheckedChange = { onPolicyChange(policy.copy(requireApprovedNipPrefix = it)) }
                    )
                }

                if (policy.requireApprovedNipPrefix) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = policy.approvedNipPrefix,
                        onValueChange = { onPolicyChange(policy.copy(approvedNipPrefix = it)) },
                        label = { Text("Awalan NIP yang Diizinkan (Contoh: 19 atau GURU-)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Card 2: Ketentuan Masuk Peran Siswa
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Ketentuan Masuk Peran Siswa",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Konfigurasi aturan kelas, mode kerja, dan kode akses kelas bagi peserta didik.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Izinkan Pendaftaran Mandiri Siswa",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Jika dinonaktifkan, hanya akun yang didaftarkan Admin yang dapat masuk",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = policy.allowStudentSelfRegistration,
                        onCheckedChange = { onPolicyChange(policy.copy(allowStudentSelfRegistration = it)) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Wajibkan Kode Akses Kelas",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Siswa wajib memasukkan token kelas saat login",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = policy.requireClassAccessCode,
                        onCheckedChange = { onPolicyChange(policy.copy(requireClassAccessCode = it)) }
                    )
                }

                if (policy.requireClassAccessCode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = policy.classAccessCode,
                        onValueChange = { onPolicyChange(policy.copy(classAccessCode = it)) },
                        label = { Text("Kode Akses Kelas Aktif") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = policy.allowedStudentClasses,
                    onValueChange = { onPolicyChange(policy.copy(allowedStudentClasses = it)) },
                    label = { Text("Daftar Kelas yang Diizinkan (Pisahkan dengan koma)") },
                    placeholder = { Text("Contoh: X-1, X-2, X-3, X E-1") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Mode Pengerjaan E-LKPD yang Diizinkan:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = policy.allowIndividuMode,
                        onClick = {
                            val next = !policy.allowIndividuMode
                            if (next || policy.allowKelompokMode) {
                                onPolicyChange(policy.copy(allowIndividuMode = next))
                            }
                        },
                        label = { Text("Mode Individu") }
                    )
                    FilterChip(
                        selected = policy.allowKelompokMode,
                        onClick = {
                            val next = !policy.allowKelompokMode
                            if (next || policy.allowIndividuMode) {
                                onPolicyChange(policy.copy(allowKelompokMode = next))
                            }
                        },
                        label = { Text("Mode Kelompok Diskusi") }
                    )
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onResetPolicy,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset Default")
            }
            Button(
                onClick = onSavePolicy,
                modifier = Modifier
                    .weight(1.3f)
                    .testTag("btn_save_admin_policy")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simpan Ketentuan")
            }
        }

        if (onSwitchRoleClick != null) {
            FilledTonalButton(
                onClick = onSwitchRoleClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Uji / Masuk ke Halaman Pemilihan Peran Sekarang")
            }
        }
    }
}

@Composable
private fun ModuleAndKkmConfigSection(
    policy: AdminRoleAccessPolicy,
    onPolicyChange: (AdminRoleAccessPolicy) -> Unit,
    onSavePolicy: () -> Unit,
    onOpenRubricConfig: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Identitas Institusi & Standar Ketuntasan (KKM)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = policy.schoolName,
                    onValueChange = { onPolicyChange(policy.copy(schoolName = it)) },
                    label = { Text("Nama Satuan Pendidikan / Sekolah") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = policy.academicYear,
                    onValueChange = { onPolicyChange(policy.copy(academicYear = it)) },
                    label = { Text("Tahun Ajaran Aktif") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = policy.announcementBanner,
                    onValueChange = { onPolicyChange(policy.copy(announcementBanner = it)) },
                    label = { Text("Pesan Pengumuman Kelas (Ditampilkan di Portal)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Batas Kriteria Ketuntasan Minimal (KKM HOTS): ${policy.minimumPassingGradeKkm} Poin",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = policy.minimumPassingGradeKkm.toFloat(),
                    onValueChange = { onPolicyChange(policy.copy(minimumPassingGradeKkm = it.toInt())) },
                    valueRange = 55f..90f,
                    steps = 6
                )
                Text(
                    text = "Siswa dengan skor HOTS di bawah ${policy.minimumPassingGradeKkm} otomatis ditandai status 'PERLU REMEDI'.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Buka Akses Evaluasi Akhir HOTS",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Izinkan siswa mengerjakan Pilihan Ganda & Essay",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = policy.isEvaluationOpen,
                        onCheckedChange = { onPolicyChange(policy.copy(isEvaluationOpen = it)) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Aktifkan Pendampingan AI Tutor Parijoto",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Menyediakan pertanyaan pemantik tanpa membocorkan jawaban",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = policy.isAiTutorEnabled,
                        onCheckedChange = { onPolicyChange(policy.copy(isAiTutorEnabled = it)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSavePolicy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Pengaturan Kurikulum & KKM")
                }

                if (onOpenRubricConfig != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onOpenRubricConfig,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buka Panel Rubrik 6 Aspek & URL Media E-LKPD")
                    }
                }
            }
        }
    }
}

@Composable
private fun UserRoleManagementSection(
    users: List<User>,
    onToggleRole: (User) -> Unit,
    onDeleteUser: (Int) -> Unit,
    onAddUserClick: () -> Unit,
    onSwitchRoleClick: (() -> Unit)?
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Manajemen Akun & Otorisasi Peran",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Ubah peran pengguna antara Siswa dan Guru/Administrator, atau tambahkan akun baru secara langsung.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onAddUserClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tambah Akun")
                        }
                        if (onSwitchRoleClick != null) {
                            OutlinedButton(
                                onClick = onSwitchRoleClick,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ganti Sesi Masuk")
                            }
                        }
                    }
                }
            }
        }

        items(users, key = { it.id }) { user ->
            val isTeacher = user.role == UserRole.TEACHER
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isTeacher) MaterialTheme.colorScheme.secondaryContainer
                                    else MaterialTheme.colorScheme.primaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isTeacher) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isTeacher) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = user.nama,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "ID/NIS: ${user.nis} • ${user.kelas}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                color = if (isTeacher) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = if (isTeacher) "PERAN: GURU / ADMIN" else "PERAN: SISWA",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTeacher) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(onClick = { onToggleRole(user) }) {
                            Text(
                                text = if (isTeacher) "Jadikan Siswa" else "Jadikan Guru",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        IconButton(onClick = { onDeleteUser(user.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Akun",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentItemCard(
    student: StudentSummary,
    kkm: Int,
    onClick: () -> Unit
) {
    val statusColor = when (student.status) {
        StudentLearningStatus.COMPLETED -> Color(0xFF2E7D32)
        StudentLearningStatus.IN_PROGRESS -> Color(0xFFF57C00)
        StudentLearningStatus.NEEDS_REMEDIAL -> Color(0xFFD32F2F)
    }

    val statusText = when (student.status) {
        StudentLearningStatus.COMPLETED -> "TUNTAS"
        StudentLearningStatus.IN_PROGRESS -> "BERJALAN"
        StudentLearningStatus.NEEDS_REMEDIAL -> "REMEDI (<$kkm)"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = statusColor
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = student.fullName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${student.className} • NISN: ${student.nisn.ifBlank { "-" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = statusColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (student.isHotsSubmitted) "Skor HOTS: ${student.hotsScore.toInt()}" else "Belum Evaluasi",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Progres E-LKPD: ${student.overallPercentage}% (Akt 1: ${student.act1Percentage}% • Akt 2: ${student.act2Percentage}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (student.overallPercentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = statusColor,
                trackColor = statusColor.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun AddNewAccountDialog(
    allowedClasses: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, UserRole) -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var nis by remember { mutableStateOf("") }
    var kelas by remember { mutableStateOf(allowedClasses.firstOrNull() ?: "X-1") }
    var role by remember { mutableStateOf(UserRole.STUDENT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Akun Pengguna Baru") },
        text = {
            Column {
                Text(
                    text = "Pilih Peran Akun:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == UserRole.STUDENT,
                        onClick = { role = UserRole.STUDENT }
                    )
                    Text("Siswa", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = role == UserRole.TEACHER,
                        onClick = { role = UserRole.TEACHER }
                    )
                    Text("Guru / Admin", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nis,
                    onValueChange = { nis = it },
                    label = { Text(if (role == UserRole.STUDENT) "NIS / NISN" else "NIP / ID Guru") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (role == UserRole.STUDENT) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = kelas,
                        onValueChange = { kelas = it },
                        label = { Text("Kelas") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nama, nis, kelas, role) },
                enabled = nama.isNotBlank() && nis.isNotBlank()
            ) {
                Text("Simpan Akun")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
