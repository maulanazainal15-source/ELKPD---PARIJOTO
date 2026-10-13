package com.example.ui.screens.survey

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SurveyViewModelTest {

    @Test
    fun `submitSurvey calculates standard SUS score accurately when all neutral`() {
        val viewModel = SurveyViewModel()
        // Default: semua 10 item bernilai 3 (Netral)
        // Kontribusi tiap item = 2 -> Total = 20 -> 20 * 2.5 = 50.0
        viewModel.submitSurvey()

        val state = viewModel.uiState.value
        assertTrue(state.isSubmitted)
        assertEquals(50.0, state.calculatedResult?.susFinalScore ?: 0.0, 0.01)
    }

    @Test
    fun `submitSurvey calculates maximum SUS score 100 when optimal answers selected`() {
        val viewModel = SurveyViewModel()
        // Pernyataan ganjil (positif) diberi 5, pernyataan genap (negatif) diberi 1
        for (i in 1..10) {
            val optimalScore = if (i % 2 != 0) 5 else 1
            viewModel.updateScore(i, optimalScore)
        }
        viewModel.submitSurvey()

        val state = viewModel.uiState.value
        assertEquals(100.0, state.calculatedResult?.susFinalScore ?: 0.0, 0.01)
        assertTrue(state.calculatedResult?.gradeCategory?.contains("Grade A") == true)
    }
}
