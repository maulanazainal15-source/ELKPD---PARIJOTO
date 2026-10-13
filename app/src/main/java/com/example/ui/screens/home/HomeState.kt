package com.example.ui.screens.home

import com.example.domain.model.User

data class HomeState(
    val currentUser: User? = null,
    val activity1Progress: Int = 0,
    val activity2Progress: Int = 0,
    val evaluationProgress: Int = 0,
    val overallProgress: Int = 0,
    val isEvaluationCompleted: Boolean = false,
    val schoolName: String = "SMA Negeri 1 Gunung Muria",
    val adminPin: String = "2026",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
