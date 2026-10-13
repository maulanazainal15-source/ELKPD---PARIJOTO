package com.example.ui.screens.survey

import androidx.lifecycle.ViewModel
import com.example.domain.model.SurveyStatement
import com.example.domain.model.UsabilitySurveyResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SurveyUiState(
    val statements: List<SurveyStatement> = SurveyViewModel.defaultSusStatements(),
    val qualitativeComment: String = "",
    val calculatedResult: UsabilitySurveyResult? = null,
    val isSubmitted: Boolean = false
)

class SurveyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SurveyUiState())
    val uiState: StateFlow<SurveyUiState> = _uiState.asStateFlow()

    fun updateScore(statementId: Int, newScore: Int) {
        val coercedScore = newScore.coerceIn(1, 5)
        _uiState.update { state ->
            val updatedList = state.statements.map { item ->
                if (item.id == statementId) item.copy(selectedScore = coercedScore) else item
            }
            state.copy(statements = updatedList)
        }
    }

    fun updateQualitativeComment(comment: String) {
        _uiState.update { it.copy(qualitativeComment = comment) }
    }

    /**
     * Menghitung skor akhir System Usability Scale (SUS) dengan rumus standar Brooke (1996):
     * - Pernyataan positif (ganjil): (Skor - 1)
     * - Pernyataan negatif (genap): (5 - Skor)
     * Total kontribusi dikalikan 2.5 untuk mendapatkan rentang 0 - 100.
     */
    fun submitSurvey() {
        val currentStatements = _uiState.value.statements
        var rawSum = 0

        for (item in currentStatements) {
            val contribution = if (item.isPositiveStatement) {
                item.selectedScore - 1
            } else {
                5 - item.selectedScore
            }
            rawSum += contribution
        }

        val finalSusScore = rawSum * 2.5
        val category = when {
            finalSusScore >= 85.0 -> "Grade A (Best Imaginable / Sangat Layak)"
            finalSusScore >= 72.0 -> "Grade B (Excellent / Layak)"
            finalSusScore >= 52.0 -> "Grade C (Good / Cukup Layak)"
            else -> "Needs Improvement (Perlu Perbaikan)"
        }

        val result = UsabilitySurveyResult(
            susFinalScore = finalSusScore,
            gradeCategory = category,
            qualitativeFeedback = _uiState.value.qualitativeComment.trim()
        )

        _uiState.update {
            it.copy(
                calculatedResult = result,
                isSubmitted = true
            )
        }
    }

    fun resetSurvey() {
        _uiState.value = SurveyUiState()
    }

    companion object {
        fun defaultSusStatements(): List<SurveyStatement> = listOf(
            SurveyStatement(1, "Saya merasa senang menggunakan aplikasi E-LKPD Etnosains Parijoto ini untuk belajar kimia dan biologi.", true),
            SurveyStatement(2, "Saya merasa tampilan dan alur navigasi aplikasi ini terlalu rumit untuk dipahami.", false),
            SurveyStatement(3, "Fitur Laboratorium Virtual, AR Molekul 3D, dan Eksplorasi Parijoto mudah dioperasikan.", true),
            SurveyStatement(4, "Saya membutuhkan bantuan teknisi atau guru terus-menerus untuk bisa menggunakan aplikasi ini.", false),
            SurveyStatement(5, "Integrasi materi kearifan lokal Gunung Muria dengan konsep sains ilmiah tersaji dengan sangat baik.", true),
            SurveyStatement(6, "Saya merasa banyak ketidakkonsistenan antara materi, praktikum, dan kuis di dalam aplikasi ini.", false),
            SurveyStatement(7, "Saya yakin teman-teman sekelas akan cepat mahir menggunakan aplikasi E-LKPD ini.", true),
            SurveyStatement(8, "Aplikasi ini terasa membebani dan menghambat proses diskusi kelompok kami.", false),
            SurveyStatement(9, "Saya merasa lebih percaya diri dalam menganalisis masalah HOTS setelah belajar dengan aplikasi ini.", true),
            SurveyStatement(10, "Saya harus mempelajari banyak hal teknis terlebih dahulu sebelum bisa memulai pembelajaran.", false)
        )
    }
}
