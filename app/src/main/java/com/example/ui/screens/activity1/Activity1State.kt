package com.example.ui.screens.activity1

data class Activity1State(
    val currentPhase: Int = 1,
    val orientasiAnswer: String = "",
    val organisasiAnswer: String = "",
    val penyelidikanAnswer: String = "",
    val karyaAnswer: String = "",
    val evaluasiAnswer: String = "",
    val progressPercentage: Int = 0,
    val isSaved: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
