package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ui.screens.CpAndTpSection
import com.example.ui.screens.PendahuluanSection
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.TeacherDashboardDialog
import com.example.ui.screens.activity1.Activity1Screen
import com.example.ui.screens.activity2.Activity2Screen
import com.example.ui.screens.analytics.AnalyticsDashboardScreen
import com.example.ui.screens.ar.ArParijotoScreen
import com.example.ui.screens.auth.IdentityScreen
import com.example.ui.screens.bibliography.BibliographyScreen
import com.example.ui.screens.discussion.DiscussionBoardScreen
import com.example.ui.screens.evaluation.EvaluationEssayScreen
import com.example.ui.screens.evaluation.EvaluationPgScreen
import com.example.ui.screens.evaluation.EvaluationResultScreen
import com.example.ui.screens.gamification.GamificationScreen
import com.example.ui.screens.glosarium.GlosariumScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.quiz.QuizDuelScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.survey.FeedbackSurveyScreen
import com.example.ui.screens.teacher.TeacherDashboardScreen
import com.example.viewmodel.ElpkdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParijotoNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    elpkdViewModel: ElpkdViewModel = viewModel()
) {
    val activeUser by elpkdViewModel.activeUser.collectAsStateWithLifecycle()
    val allUsers by elpkdViewModel.allUsers.collectAsStateWithLifecycle()
    val allProgress by elpkdViewModel.allProgress.collectAsStateWithLifecycle()
    val allAnswers by elpkdViewModel.allAnswers.collectAsStateWithLifecycle()
    val allResults by elpkdViewModel.allResults.collectAsStateWithLifecycle()
    val teacherConfig by elpkdViewModel.teacherConfig.collectAsStateWithLifecycle()

    val isAiLoading by elpkdViewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiTutorResponse by elpkdViewModel.aiTutorResponse.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier.padding(paddingValues)
    ) {
        // 1. Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // 2. Identity / Login Screen
        composable(Screen.Identity.route) {
            IdentityScreen(
                navController = navController,
                onRegisteredCallback = { nama, nis, kelas, kelompok, mode ->
                    elpkdViewModel.saveStudentIdentity(nama, nis, kelas, kelompok, mode)
                }
            )
        }

        // 3. Home Screen
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // 4. Guide Screen
        composable(Screen.Guide.route) {
            PendahuluanSection(
                config = teacherConfig,
                onCompleteAndStart = {
                    elpkdViewModel.markSectionCompleted("PENDAHULUAN")
                    navController.navigate(Screen.Activity1.route)
                }
            )
        }

        // 5. CP & TP Screen
        composable(Screen.CpTp.route) {
            CpAndTpSection(
                onCompleteAndContinue = {
                    elpkdViewModel.markSectionCompleted("CP_TP")
                    navController.navigate(Screen.Activity1.route)
                }
            )
        }

        // 5b. Daftar Pustaka Screen (Read-Only untuk Siswa)
        composable(Screen.Bibliography.route) {
            BibliographyScreen(
                onBackClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 6. AI Tutor Screen
        composable(Screen.AiTutor.route) {
            AiTutorScreenContent(
                tutorResponse = aiTutorResponse,
                isLoading = isAiLoading,
                onAskQuestion = { q ->
                    elpkdViewModel.askAiTutorQuestion(q, "Eksplorasi Etnosains Parijoto Gunung Muria")
                }
            )
        }

        // 7. Profile Screen (Phase 11)
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        // 8. Activity 1 (Eksplorasi Etnosains Parijoto - Phase 8)
        composable(Screen.Activity1.route) {
            Activity1Screen(navController = navController)
        }

        // 9. Activity 2 (Praktikum & Rekonstruksi Sains - Phase 9)
        composable(Screen.Activity2.route) {
            Activity2Screen(navController = navController)
        }

        // 10. Evaluation Multiple Choice (Phase 10)
        composable(Screen.EvaluationPg.route) {
            EvaluationPgScreen(navController = navController)
        }

        // 11. Evaluation Essay (Phase 10)
        composable(Screen.EvaluationEssay.route) {
            EvaluationEssayScreen(navController = navController)
        }

        // 12. Result Screen (Phase 10)
        composable(Screen.Result.route) {
            EvaluationResultScreen(navController = navController)
        }

        // 12b. Glosarium Etnosains Screen (Phase 12)
        composable(Screen.Glosarium.route) {
            GlosariumScreen(onBackClick = { navController.navigateUp() })
        }

        // 12c. Augmented Reality (AR) Molekul Parijoto (Phase 14)
        composable(Screen.ArParijoto.route) {
            ArParijotoScreen(onBackClick = { navController.navigateUp() })
        }

        // 12d. Gamifikasi, Lencana & Rekomendasi Belajar Adaptif (Phase 15)
        composable(Screen.Gamification.route) {
            GamificationScreen(navController = navController)
        }

        // 12e. Forum Diskusi Kelas Etnosains (Phase 17)
        composable(Screen.DiscussionBoard.route) {
            DiscussionBoardScreen(onBackClick = { navController.navigateUp() })
        }

        // 12f. Tantangan Quiz Duel Etnosains (Phase 17)
        composable(Screen.QuizDuel.route) {
            QuizDuelScreen(onBackClick = { navController.navigateUp() })
        }

        // 12g. Dashboard Analitik Capaian HOTS (Phase 18)
        composable(Screen.AnalyticsDashboard.route) {
            AnalyticsDashboardScreen(onBackClick = { navController.navigateUp() })
        }

        // 12h. Survei Evaluasi Usabilitas & Respon Siswa (Phase 20)
        composable(Screen.FeedbackSurvey.route) {
            FeedbackSurveyScreen(onBackClick = { navController.navigateUp() })
        }

        // 13. Teacher Dashboard (Phase 16)
        composable(Screen.TeacherDashboard.route) {
            TeacherDashboardScreen(
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home.route)
                    }
                },
                onStudentClick = { userId ->
                    navController.navigate(Screen.StudentDetail.createRoute(userId))
                },
                onSwitchRoleClick = {
                    navController.navigate(Screen.Identity.route)
                },
                onOpenRubricConfigClick = {
                    navController.navigate(Screen.ContentConfig.route)
                }
            )
        }

        // 14. Student Detail (Teacher Mode)
        composable(
            route = Screen.StudentDetail.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Column(modifier = Modifier.fillMaxSize()) {
                SubScreenTopBar(
                    title = "Detail Siswa #$userId",
                    onBack = { navController.popBackStack() }
                )
                ResultScreen(
                    activeUser = allUsers.find { it.id == userId } ?: activeUser,
                    config = teacherConfig,
                    progressList = allProgress.filter { it.userId == userId },
                    answersMap = allAnswers.filter { it.userId == userId }.associateBy { it.questionId },
                    resultsHistory = allResults.filter { it.userId == userId },
                    onRetakeEvaluation = { navController.popBackStack() },
                    onShowMessage = { elpkdViewModel.showStatusMessage(it) }
                )
            }
        }

        // 15. Content Config (Teacher Mode)
        composable(Screen.ContentConfig.route) {
            TeacherDashboardDialog(
                config = teacherConfig,
                allUsers = allUsers,
                allProgress = allProgress,
                allAnswers = allAnswers,
                allResults = allResults,
                onGradeStudentResult = { res, scores, note ->
                    elpkdViewModel.submitTeacherManualGrade(res, scores, note)
                },
                onUpdateConfig = { newCfg -> elpkdViewModel.updateTeacherConfiguration(newCfg) },
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubScreenTopBar(
    title: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        )
    )
}

@Composable
private fun AiTutorScreenContent(
    tutorResponse: String,
    isLoading: Boolean,
    onAskQuestion: (String) -> Unit
) {
    var questionInput by remember { mutableStateOf("") }
    val quickPrompts = listOf(
        "Bagaimana kaitan over-harvesting Parijoto dengan rantai makanan hutan Muria?",
        "Jelaskan peran flavonoid, saponin, tanin, dan β-sitosterol secara ilmiah!",
        "Mengapa status Indikasi Geografis (IG) dan budidaya ex-situ penting?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "AI Tutor Etnosains Parijoto",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Bimbingan Pertanyaan Pemantik & Konsep Ilmiah (Tanpa Membocorkan Kunci Jawaban)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Text(
            text = "Pilih Topik Pemantik Diskusi:",
            style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
        )

        quickPrompts.forEach { prompt ->
            FilledTonalButton(
                onClick = {
                    questionInput = prompt
                    onAskQuestion(prompt)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Menyusun penjelasan konsep & pertanyaan pemantik...")
                    }
                } else {
                    Text(
                        text = tutorResponse,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = questionInput,
                onValueChange = { questionInput = it },
                placeholder = { Text("Tanyakan konsep biologi atau etnosains Parijoto...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onAskQuestion(questionInput) },
                enabled = questionInput.isNotBlank() && !isLoading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(52.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Kirim")
            }
        }
    }
}
