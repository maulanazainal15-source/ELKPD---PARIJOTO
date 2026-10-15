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
    fun quizDuelScreen_displaysLobbyAndLeaderboard() {
        composeTestRule.setContent {
            QuizDuelScreen(onBackClick = {})
        }

        // 1. Verifikasi judul Arena dan tombol Matchmaking
        composeTestRule
            .onNodeWithText("Arena Quiz Duel Etnosains")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Realtime Quiz Duel")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Cari Lawan Duel Online (Matchmaking)")
            .assertIsDisplayed()

        // 2. Verifikasi komponen Leaderboard muncul
        composeTestRule
            .onNodeWithText("Papan Skor Realtime (Leaderboard)")
            .assertIsDisplayed()
    }

    @Test
    fun quizDuelScreen_clickMatchmaking_showsConnectingState() {
        composeTestRule.setContent {
            QuizDuelScreen(onBackClick = {})
        }

        // Klik tombol Matchmaking
        composeTestRule
            .onNodeWithText("Cari Lawan Duel Online (Matchmaking)")
            .performClick()

        // Verifikasi transisi ke state pencarian room cloud
        composeTestRule
            .onNodeWithText("Menghubungkan ke Cloud Room...")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Batalkan Pencarian")
            .assertIsDisplayed()
    }
}
