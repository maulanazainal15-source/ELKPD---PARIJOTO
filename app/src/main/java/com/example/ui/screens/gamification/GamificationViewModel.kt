package com.example.ui.screens.gamification

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.model.Badge
import com.example.domain.model.BadgeCategory
import com.example.domain.model.Recommendation
import com.example.domain.model.RecommendationPriority
import com.example.domain.model.UserGamificationProfile
import com.example.domain.repository.EvaluationRepository
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.UserRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class GamificationUiState(
    val profile: UserGamificationProfile = UserGamificationProfile(userId = -1),
    val recommendations: List<Recommendation> = emptyList(),
    val isLoading: Boolean = true
)

class GamificationViewModel(
    private val userRepository: UserRepository,
    private val learningRepository: LearningRepository,
    private val evaluationRepository: EvaluationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamificationUiState())
    val uiState: StateFlow<GamificationUiState> = _uiState.asStateFlow()

    init {
        loadGamificationData()
    }

    private fun loadGamificationData() {
        viewModelScope.launch {
            userRepository.getAllStudents().collect { users ->
                val activeUserId = users.lastOrNull()?.id ?: 1

                combine(
                    learningRepository.getProgressByUserAndActivity(activeUserId, "ACT_1"),
                    learningRepository.getProgressByUserAndActivity(activeUserId, "ACT_2"),
                    evaluationRepository.getResultByUser(activeUserId)
                ) { act1, act2, eval ->
                    val act1P = act1?.percentage ?: 0
                    val act2P = act2?.percentage ?: 0
                    val hotsS = eval?.pgScore ?: 0.0

                    val calculatedXp = (act1P * 2) + (act2P * 3) + (hotsS * 5).toInt()
                    val level = (calculatedXp / 200) + 1
                    val levelTitle = getLevelTitle(level)

                    val badges = evaluateBadges(act1P, act2P, hotsS)
                    val recommendations = generateRecommendations(act1P, act2P, hotsS, eval?.isSubmitted ?: false)

                    GamificationUiState(
                        profile = UserGamificationProfile(
                            userId = activeUserId,
                            totalXp = calculatedXp,
                            currentLevel = level,
                            levelTitle = levelTitle,
                            unlockedBadges = badges,
                            nextLevelXpThreshold = level * 200
                        ),
                        recommendations = recommendations,
                        isLoading = false
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            }
        }
    }

    private fun getLevelTitle(level: Int): String {
        return when (level) {
            1 -> "Siswa Magang Etnosains"
            2 -> "Peneliti Antosianin Muda"
            3 -> "Pengamat Kultur Kudus"
            4 -> "Ahli Kimia Parijoto"
            else -> "Master Etnosaintis Muria"
        }
    }

    private fun evaluateBadges(act1P: Int, act2P: Int, hotsScore: Double): List<Badge> {
        return listOf(
            Badge(
                id = "badge_1",
                title = "Penyelidik Jalak",
                description = "Menyelesaikan Sintaks Problem-Based Learning pada Aktivitas 1",
                iconName = "Assignment",
                category = BadgeCategory.EXPLORATION,
                isUnlocked = act1P >= 100
            ),
            Badge(
                id = "badge_2",
                title = "Ekstraktor Parijoto",
                description = "Menyelesaikan Praktikum Ekstraksi Pigmen Alami pada Aktivitas 2",
                iconName = "Science",
                category = BadgeCategory.LABORATORY,
                isUnlocked = act2P >= 100
            ),
            Badge(
                id = "badge_3",
                title = "Cendekiawan Muria",
                description = "Meraih Skor Evaluasi HOTS di atas 80",
                iconName = "EmojiEvents",
                category = BadgeCategory.EXCELLENCE,
                isUnlocked = hotsScore >= 80.0
            ),
            Badge(
                id = "badge_4",
                title = "Pelestari Budaya",
                description = "Menguasai Pemetaan Sainmas dan Glosarium Krama Inggil",
                iconName = "AutoStories",
                category = BadgeCategory.CULTURE,
                isUnlocked = act2P >= 50 && act1P >= 50
            )
        )
    }

    private fun generateRecommendations(act1P: Int, act2P: Int, hotsScore: Double, isSubmitted: Boolean): List<Recommendation> {
        val list = mutableListOf<Recommendation>()

        if (act1P < 100) {
            list.add(
                Recommendation(
                    id = "rec_1",
                    title = "Lanjutkan Aktivitas 1 (PBL)",
                    description = "Selesaikan orientasi masalah keasaman buah Parijoto untuk memahami antosianin.",
                    targetRoute = Screen.Activity1.route,
                    priority = RecommendationPriority.HIGH
                )
            )
        }

        if (act1P >= 80 && act2P < 100) {
            list.add(
                Recommendation(
                    id = "rec_2",
                    title = "Lakukan Praktikum Aktivitas 2",
                    description = "Lakukan pengujian pH indikator alami menggunakan e-lab rekonstruksi sains.",
                    targetRoute = Screen.Activity2.route,
                    priority = RecommendationPriority.HIGH
                )
            )
        }

        if (act2P >= 100 && !isSubmitted) {
            list.add(
                Recommendation(
                    id = "rec_3",
                    title = "Uji Cipta Evaluasi HOTS",
                    description = "Uji pemahaman sains modern dan kearifan lokal Anda pada Evaluasi HOTS.",
                    targetRoute = Screen.EvaluationPg.route,
                    priority = RecommendationPriority.MEDIUM
                )
            )
        }

        if (isSubmitted && hotsScore < 75.0) {
            list.add(
                Recommendation(
                    id = "rec_4",
                    title = "Ulasan Glosarium & Sainmas",
                    description = "Pelajari kembali hubungan struktur antosianin dan keasaman di Glosarium.",
                    targetRoute = Screen.Glosarium.route,
                    priority = RecommendationPriority.HIGH
                )
            )
        }

        return list
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                GamificationViewModel(
                    userRepository = RepositoryModule.provideUserRepository(app),
                    learningRepository = RepositoryModule.provideLearningRepository(app),
                    evaluationRepository = RepositoryModule.provideEvaluationRepository(app)
                )
            }
        }
    }
}
