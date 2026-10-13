package com.example.domain.model

data class LearningProgress(
    val id: Int = 0,
    val userId: Int,
    val activityId: String,
    val percentage: Int,
    val isCompleted: Boolean = percentage >= 100,
    val lastUpdated: Long = System.currentTimeMillis()
)
