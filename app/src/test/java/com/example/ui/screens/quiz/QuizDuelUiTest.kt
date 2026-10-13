package com.example.ui.screens.quiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class QuizDuelUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun quizDuelScreen_displaysInitialQuestion_andAdvancesToNextQuestion() {
        composeTestRule.setContent {
            QuizDuelScreen(onBackClick = {})
        }

        // 1. Verifikasi soal pertama muncul di layar
        composeTestRule
            .onNodeWithText("Soal 1 dari 2")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pelarut apakah yang paling efektif untuk mengekstrak pigmen antosianin Parijoto?")
            .assertIsDisplayed()

        // 2. Pilih opsi jawaban benar ("Etanol terasamkan")
        composeTestRule
            .onNodeWithText("Etanol terasamkan")
            .performClick()

        // 3. Verifikasi layar berpindah ke Soal ke-2
        composeTestRule
            .onNodeWithText("Soal 2 dari 2")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Perubahan warna antosianin menjadi merah cerah terjadi pada kondisi pH...")
            .assertIsDisplayed()
    }

    @Test
    fun quizDuelScreen_completesQuiz_andDisplaysFinalScore() {
        composeTestRule.setContent {
            QuizDuelScreen(onBackClick = {})
        }

        // Jawab Soal 1
        composeTestRule.onNodeWithText("Etanol terasamkan").performClick()

        // Jawab Soal 2
        composeTestRule.onNodeWithText("Asam (pH < 3)").performClick()

        // Verifikasi tampilan selesai muncul
        composeTestRule.onNodeWithText("Tantangan Selesai! 🎉").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kembali ke Beranda").assertIsDisplayed()
    }
}
