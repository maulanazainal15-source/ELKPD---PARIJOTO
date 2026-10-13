package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.ui.components.TeacherPinVerificationDialog
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.ui.components.LearningProgressBar
import com.example.ui.components.ResponsiveLayoutContainer
import com.example.ui.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    ResponsiveLayoutContainer(
        compactContent = {
            HomeScreenBody(state = state, navController = navController)
        }
    )
}

@Composable
private fun HomeScreenBody(
    state: HomeState,
    navController: NavController
) {
    var showTeacherPinDialog by remember { mutableStateOf(false) }

    if (showTeacherPinDialog) {
        TeacherPinVerificationDialog(
            currentActivePin = state.adminPin,
            onDismiss = { showTeacherPinDialog = false },
            onPinSuccess = {
                showTeacherPinDialog = false
                navController.navigate(Screen.TeacherDashboard.route)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header Banner Profil & Identitas Muria
        HeaderSection(
            user = state.currentUser,
            schoolName = state.schoolName,
            onOpenTeacherPortal = {
                if (state.currentUser?.role == UserRole.TEACHER) {
                    navController.navigate(Screen.TeacherDashboard.route)
                } else {
                    showTeacherPinDialog = true
                }
            }
        )

        Column(modifier = Modifier.padding(20.dp)) {
            // 2. Card Progres Pembelajaran Keseluruhan
            OverallProgressCard(
                overallProgress = state.overallProgress,
                onViewResultClick = { navController.navigate(Screen.Result.route) },
                isCompleted = state.isEvaluationCompleted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Modul Aktivitas PBL Utama
            Text(
                text = "Modul Aktivitas Pembelajaran (PBL)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Card 1: Eksplorasi Etnosains Parijoto
            ActivityModuleCard(
                title = "Aktivitas 1: Eksplorasi Etnosains",
                subtitle = "Orientasi masalah etnosains Parijoto lereng Gunung Muria",
                icon = Icons.Default.Explore,
                progressPercentage = state.activity1Progress,
                badgeText = "Fase 1 - 5 PBL",
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = { navController.navigate(Screen.Activity1.route) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Card 2: Praktikum & Rekonstruksi Sains
            ActivityModuleCard(
                title = "Aktivitas 2: Rekonstruksi Sains",
                subtitle = "Penyelidikan laboratorium & transformasi pengetahuan asli",
                icon = Icons.Default.Biotech,
                progressPercentage = state.activity2Progress,
                badgeText = "Praktikum & Analisis",
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { navController.navigate(Screen.Activity2.route) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Card 3: Evaluasi Pembelajaran
            ActivityModuleCard(
                title = "Evaluasi Pembelajaran HOTS",
                subtitle = "Uji pemahaman Pilihan Ganda dan Essay Terintegrasi",
                icon = Icons.Default.Assignment,
                progressPercentage = state.evaluationProgress,
                badgeText = "Tes Akhir",
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                onClick = { navController.navigate(Screen.EvaluationPg.route) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Fitur Cepat & Alat Bantu (Quick Actions Grid)
            Text(
                text = "Fitur & Fitur Pendukung",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "AI Tutor Parijoto",
                    icon = Icons.Default.AutoAwesome,
                    modifier = Modifier.weight(1f),
                    badgeColor = MaterialTheme.colorScheme.secondary,
                    onClick = { navController.navigate(Screen.AiTutor.route) }
                )
                QuickActionCard(
                    title = "Panduan & Petunjuk",
                    icon = Icons.Default.Book,
                    modifier = Modifier.weight(1f),
                    badgeColor = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate(Screen.Guide.route) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Capaian (CP & TP)",
                    icon = Icons.Default.School,
                    modifier = Modifier.weight(1f),
                    badgeColor = MaterialTheme.colorScheme.tertiary,
                    onClick = { navController.navigate(Screen.CpTp.route) }
                )
                QuickActionCard(
                    title = if (state.currentUser?.role == UserRole.TEACHER) "Dashboard Guru" else "Profil Saya",
                    icon = if (state.currentUser?.role == UserRole.TEACHER) Icons.Default.School else Icons.Default.Person,
                    modifier = Modifier.weight(1f),
                    badgeColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = {
                        val route = if (state.currentUser?.role == UserRole.TEACHER) {
                            Screen.TeacherDashboard.route
                        } else {
                            Screen.Profile.route
                        }
                        navController.navigate(route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Glosarium & Sainmas",
                    icon = Icons.Default.Book,
                    modifier = Modifier.weight(1f),
                    badgeColor = MaterialTheme.colorScheme.secondary,
                    onClick = { navController.navigate(Screen.Glosarium.route) }
                )
                QuickActionCard(
                    title = "AR Molekul 3D",
                    icon = Icons.Default.ViewInAr,
                    modifier = Modifier.weight(1f),
                    badgeColor = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate(Screen.ArParijoto.route) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            QuickActionCard(
                title = "Lencana & Gamifikasi XP Etnosains",
                icon = Icons.Default.EmojiEvents,
                modifier = Modifier.fillMaxWidth(),
                badgeColor = MaterialTheme.colorScheme.tertiary,
                onClick = { navController.navigate(Screen.Gamification.route) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Quiz Duel 15s",
                    icon = Icons.Default.EmojiEvents,
                    modifier = Modifier.weight(1f),
                    badgeColor = MaterialTheme.colorScheme.secondary,
                    onClick = { navController.navigate(Screen.QuizDuel.route) }
                )
                QuickActionCard(
                    title = "Forum Diskusi",
                    icon = Icons.Default.Group,
                    modifier = Modifier.weight(1f),
                    badgeColor = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate(Screen.DiscussionBoard.route) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            QuickActionCard(
                title = "Dashboard Analitik Capaian HOTS",
                icon = Icons.Default.Analytics,
                modifier = Modifier.fillMaxWidth(),
                badgeColor = MaterialTheme.colorScheme.primary,
                onClick = { navController.navigate(Screen.AnalyticsDashboard.route) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuickActionCard(
                title = "Evaluasi Usabilitas & Survei SUS",
                icon = Icons.Default.RateReview,
                modifier = Modifier.fillMaxWidth(),
                badgeColor = MaterialTheme.colorScheme.secondary,
                onClick = { navController.navigate(Screen.FeedbackSurvey.route) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuickActionCard(
                title = "Daftar Pustaka & Rujukan Etnosains",
                icon = Icons.Default.MenuBook,
                modifier = Modifier.fillMaxWidth(),
                badgeColor = MaterialTheme.colorScheme.primary,
                onClick = { navController.navigate(Screen.Bibliography.route) }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HeaderSection(
    user: User?,
    schoolName: String,
    onOpenTeacherPortal: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(top = 28.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (user?.role == UserRole.TEACHER) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                        contentDescription = "User Avatar",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (user?.role == UserRole.TEACHER) "Mode Guru / Administrator," else "Selamat Datang,",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    )
                    Text(
                        text = user?.nama ?: "Siswa E-LKPD",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp
                        )
                    )
                }

                if (user?.role == UserRole.TEACHER) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .clickable { onOpenTeacherPortal() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Mode Guru",
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GURU / ADMIN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = user?.kelas ?: "SISWA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onOpenTeacherPortal,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Akses Guru",
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (user != null && user.kelompok.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mode: ${user.mode.name} | ${user.kelompok}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallProgressCard(
    overallProgress: Int,
    onViewResultClick: () -> Unit,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Progres Pembelajaran",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                if (isCompleted) {
                    Text(
                        text = "Lihat Hasil",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onViewResultClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LearningProgressBar(
                progressPercentage = overallProgress,
                label = "Total Kelengkapan E-LKPD"
            )
        }
    }
}

@Composable
private fun ActivityModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    progressPercentage: Int,
    badgeText: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Buka Modul",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LearningProgressBar(
                progressPercentage = progressPercentage,
                label = badgeText
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            )
        }
    }
}
