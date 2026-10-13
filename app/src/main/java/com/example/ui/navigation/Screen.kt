package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    // Flow Awal & Autentikasi
    object Splash : Screen("splash_screen")
    object Identity : Screen("identity_screen")

    // Navigation Bottom Bar Destinations
    object Home : Screen("home_screen", "Beranda", Icons.Default.Home)
    object Guide : Screen("guide_screen", "Panduan", Icons.Default.Book)
    object CpTp : Screen("cptp_screen", "CP & TP", Icons.Default.Assignment)
    object AiTutor : Screen("ai_tutor_screen", "AI Tutor", Icons.Default.AutoAwesome)
    object Profile : Screen("profile_screen", "Profil", Icons.Default.Person)

    // Flow E-LKPD Utama (PBL Activities & Glosarium)
    object Activity1 : Screen("activity_1_screen")
    object Activity2 : Screen("activity_2_screen")
    object EvaluationPg : Screen("evaluation_pg_screen")
    object EvaluationEssay : Screen("evaluation_essay_screen")
    object Result : Screen("result_screen")
    object Glosarium : Screen("glosarium_screen")
    object ArParijoto : Screen("ar_parijoto_screen")
    object Gamification : Screen("gamification_screen")
    object DiscussionBoard : Screen("discussion_board_screen")
    object QuizDuel : Screen("quiz_duel_screen")
    object AnalyticsDashboard : Screen("analytics_dashboard_screen")
    object FeedbackSurvey : Screen("feedback_survey_screen")

    // Flow Guru / Admin Mode
    object TeacherDashboard : Screen("teacher_dashboard_screen")
    object StudentDetail : Screen("student_detail_screen/{userId}") {
        fun createRoute(userId: Int) = "student_detail_screen/$userId"
    }
    object ContentConfig : Screen("content_config_screen")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Guide,
    Screen.CpTp,
    Screen.AiTutor,
    Screen.Profile
)
