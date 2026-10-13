package com.example.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.HotsAnalytics
import com.example.domain.model.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val analyticsData: HotsAnalytics? = null,
    val syncStatus: SyncStatus = SyncStatus.IDLE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AnalyticsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
    }

    fun loadAnalyticsData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val quizAvg = 88.5f
            val pblScore = 90.0f
            val forumScore = 80.0f
            val hotsIndex = (0.4f * quizAvg) + (0.4f * pblScore) + (0.2f * forumScore)

            val categoryStr = when {
                hotsIndex >= 85f -> "Sangat Tinggi"
                hotsIndex >= 70f -> "Tinggi"
                hotsIndex >= 55f -> "Cukup"
                else -> "Perlu Bimbingan"
            }

            val mockAnalytics = HotsAnalytics(
                studentId = "STD_2026_001",
                studentName = "Ahmad Budi",
                averageQuizScore = quizAvg,
                practicalPblScore = pblScore,
                forumParticipationScore = forumScore,
                totalHotsIndex = hotsIndex,
                category = categoryStr,
                completedModulesCount = 4,
                totalModulesCount = 5
            )

            _uiState.update {
                it.copy(
                    analyticsData = mockAnalytics,
                    isLoading = false
                )
            }
        }
    }
}
