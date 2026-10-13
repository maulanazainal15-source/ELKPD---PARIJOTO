package com.example.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiTutorBottomSheet
import com.example.ui.screens.Aktivitas1Section
import com.example.ui.screens.Aktivitas2Section
import com.example.ui.screens.BerandaScreen
import com.example.ui.screens.CpAndTpSection
import com.example.ui.screens.EvaluationScreen
import com.example.ui.screens.PendahuluanSection
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TeacherDashboardDialog
import com.example.viewmodel.ActivitySubTab
import com.example.viewmodel.ElpkdViewModel
import com.example.viewmodel.MainNavTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElpkdAppNavigation(viewModel: ElpkdViewModel) {
    val showSplash by viewModel.showSplash.collectAsStateWithLifecycle()
    val selectedMainTab by viewModel.selectedMainTab.collectAsStateWithLifecycle()
    val selectedActivitySubTab by viewModel.selectedActivitySubTab.collectAsStateWithLifecycle()
    val isTeacherDashboardOpen by viewModel.isTeacherDashboardOpen.collectAsStateWithLifecycle()
    val isAiTutorSheetOpen by viewModel.isAiTutorSheetOpen.collectAsStateWithLifecycle()
    val aiTutorResponse by viewModel.aiTutorResponse.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val statusBannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allProgress by viewModel.allProgress.collectAsStateWithLifecycle()
    val allAnswers by viewModel.allAnswers.collectAsStateWithLifecycle()
    val allResults by viewModel.allResults.collectAsStateWithLifecycle()
    val teacherConfig by viewModel.teacherConfig.collectAsStateWithLifecycle()

    val userProgressList by viewModel.userProgressList.collectAsStateWithLifecycle()
    val userAnswersMap by viewModel.userAnswersMap.collectAsStateWithLifecycle()
    val userImages by viewModel.userImages.collectAsStateWithLifecycle()
    val userResults by viewModel.userResults.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusBannerMessage) {
        statusBannerMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    if (showSplash) {
        SplashScreen(
            config = teacherConfig,
            onStartClicked = { viewModel.dismissSplash() }
        )
        return
    }

    if (selectedMainTab != MainNavTab.BERANDA) {
        BackHandler {
            viewModel.selectMainTab(MainNavTab.BERANDA)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "E-LKPD Etnosains Parijoto",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = activeUser?.let {
                                    if (it.nama.isNotBlank()) "${it.nama} • ${it.kelas} (${it.modeKerja})"
                                    else "Kelas X / Fase E • Biologi"
                                } ?: "Kelas X / Fase E • Biologi",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.setTeacherDashboardOpen(true) },
                            modifier = Modifier.testTag("topbar_btn_guru")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Portal Guru",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )

                // Subnavigation untuk Tab Aktivitas (Pendahuluan | CP & TP | Aktivitas 1 | Aktivitas 2) sesuai Bagian AD
                if (selectedMainTab == MainNavTab.AKTIVITAS) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedActivitySubTab.ordinal,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        edgePadding = 12.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ActivitySubTab.entries.forEach { subTab ->
                            Tab(
                                selected = selectedActivitySubTab == subTab,
                                onClick = { viewModel.navigateToActivitySubTab(subTab) },
                                text = {
                                    Text(
                                        text = subTab.label,
                                        fontWeight = if (selectedActivitySubTab == subTab) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("subtab_${subTab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (teacherConfig.isAiEnabled) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.setAiTutorSheetOpen(true) },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Tutor Etnosains"
                        )
                    },
                    text = { Text("AI Tutor") },
                    modifier = Modifier.testTag("fab_ai_tutor")
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                val navItems = listOf(
                    Triple(MainNavTab.BERANDA, Icons.Default.Home, "nav_beranda"),
                    Triple(MainNavTab.AKTIVITAS, Icons.Default.Science, "nav_aktivitas"),
                    Triple(MainNavTab.EVALUASI, Icons.Default.Quiz, "nav_evaluasi"),
                    Triple(MainNavTab.HASIL, Icons.Default.Assessment, "nav_hasil"),
                    Triple(MainNavTab.PROFIL, Icons.Default.AccountCircle, "nav_profil")
                )
                navItems.forEach { (tab, icon, tag) ->
                    NavigationBarItem(
                        selected = selectedMainTab == tab,
                        onClick = { viewModel.selectMainTab(tab) },
                        icon = { Icon(icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag(tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedMainTab) {
                MainNavTab.BERANDA -> {
                    BerandaScreen(
                        activeUser = activeUser,
                        config = teacherConfig,
                        progressList = userProgressList,
                        latestResult = userResults.firstOrNull(),
                        onSaveIdentity = { nama, nis, kelas, kelompok, mode ->
                            viewModel.saveStudentIdentity(nama, nis, kelas, kelompok, mode)
                        },
                        onNavigateMainTab = { viewModel.selectMainTab(it) },
                        onNavigateSubTab = { viewModel.navigateToActivitySubTab(it) },
                        onOpenTeacherDashboard = { viewModel.setTeacherDashboardOpen(true) },
                        onSyncOfflineData = { viewModel.syncOfflineData() }
                    )
                }

                MainNavTab.AKTIVITAS -> {
                    when (selectedActivitySubTab) {
                        ActivitySubTab.PENDAHULUAN -> {
                            PendahuluanSection(
                                config = teacherConfig,
                                onCompleteAndStart = {
                                    viewModel.markSectionCompleted("PENDAHULUAN")
                                    viewModel.navigateToActivitySubTab(ActivitySubTab.CP_TP)
                                }
                            )
                        }

                        ActivitySubTab.CP_TP -> {
                            CpAndTpSection(
                                onCompleteAndContinue = {
                                    viewModel.markSectionCompleted("CP_TP")
                                    viewModel.navigateToActivitySubTab(ActivitySubTab.AKTIVITAS_1)
                                }
                            )
                        }

                        ActivitySubTab.AKTIVITAS_1 -> {
                            Aktivitas1Section(
                                config = teacherConfig,
                                answersMap = userAnswersMap,
                                onSaveAnswer = { qId, ans -> viewModel.saveAnswer(qId, ans) },
                                onRequestAiFeedback = { qId, qText, focus ->
                                    viewModel.requestAiFeedbackForSingleAnswer(qId, qText, focus)
                                },
                                onContinueToAktivitas2 = {
                                    viewModel.navigateToActivitySubTab(ActivitySubTab.AKTIVITAS_2)
                                }
                            )
                        }

                        ActivitySubTab.AKTIVITAS_2 -> {
                            Aktivitas2Section(
                                config = teacherConfig,
                                answersMap = userAnswersMap,
                                savedImages = userImages,
                                onSaveAnswer = { qId, ans -> viewModel.saveAnswer(qId, ans) },
                                onAddImage = { path, cap, part ->
                                    viewModel.addMorphologyImage(path, cap, part)
                                },
                                onDeleteImage = { id -> viewModel.deleteMorphologyImage(id) },
                                onRequestAiFeedback = { qId, qText, focus ->
                                    viewModel.requestAiFeedbackForSingleAnswer(qId, qText, focus)
                                },
                                onContinueToEvaluation = {
                                    viewModel.selectMainTab(MainNavTab.EVALUASI)
                                }
                            )
                        }
                    }
                }

                MainNavTab.EVALUASI -> {
                    EvaluationScreen(
                        config = teacherConfig,
                        answersMap = userAnswersMap,
                        isAiLoading = isAiLoading,
                        onSaveAnswer = { qId, ans -> viewModel.saveAnswer(qId, ans) },
                        onRequestAiFeedback = { qId, qText, focus ->
                            viewModel.requestAiFeedbackForSingleAnswer(qId, qText, focus)
                        },
                        onSubmitEvaluation = { useAi -> viewModel.submitEvaluation(useAi) },
                        onBackToHome = { viewModel.selectMainTab(MainNavTab.BERANDA) }
                    )
                }

                MainNavTab.HASIL -> {
                    ResultScreen(
                        activeUser = activeUser,
                        config = teacherConfig,
                        progressList = userProgressList,
                        answersMap = userAnswersMap,
                        resultsHistory = userResults,
                        onRetakeEvaluation = { viewModel.selectMainTab(MainNavTab.EVALUASI) },
                        onShowMessage = { viewModel.showStatusMessage(it) }
                    )
                }

                MainNavTab.PROFIL -> {
                    ProfileScreen(
                        activeUser = activeUser,
                        allUsers = allUsers,
                        onSaveIdentity = { nama, nis, kelas, kelompok, mode ->
                            viewModel.saveStudentIdentity(nama, nis, kelas, kelompok, mode)
                        },
                        onSwitchUser = { viewModel.switchUser(it) },
                        onCreateNewProfile = { viewModel.createNewStudentProfile() },
                        onOpenTeacherPortal = { viewModel.setTeacherDashboardOpen(true) }
                    )
                }
            }
        }
    }

    if (isAiTutorSheetOpen) {
        val currentContextLabel = when (selectedMainTab) {
            MainNavTab.AKTIVITAS -> "Aktivitas — ${selectedActivitySubTab.label} (Fase PBL ${selectedActivitySubTab.pblPhase})"
            MainNavTab.EVALUASI -> "Uji Kemampuan Evaluasi HOTS & Uraian (Fase PBL 5)"
            else -> "Eksplorasi Etnosains Parijoto Gunung Muria"
        }
        AiTutorBottomSheet(
            contextPhase = currentContextLabel,
            tutorResponse = aiTutorResponse,
            isLoading = isAiLoading,
            onAskQuestion = { q -> viewModel.askAiTutorQuestion(q, currentContextLabel) },
            onDismiss = { viewModel.setAiTutorSheetOpen(false) }
        )
    }

    if (isTeacherDashboardOpen) {
        TeacherDashboardDialog(
            config = teacherConfig,
            allUsers = allUsers,
            allProgress = allProgress,
            allAnswers = allAnswers,
            allResults = allResults,
            onGradeStudentResult = { res, scores, note ->
                viewModel.submitTeacherManualGrade(res, scores, note)
            },
            onUpdateConfig = { newCfg -> viewModel.updateTeacherConfiguration(newCfg) },
            onDismiss = { viewModel.setTeacherDashboardOpen(false) }
        )
    }
}
