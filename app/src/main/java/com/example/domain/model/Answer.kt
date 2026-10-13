package com.example.domain.model

data class Answer(
    val id: Int = 0,
    val userId: Int,
    val questionId: String,
    val activityType: String,
    val answerText: String,
    val score: Float = 0.0f,
    val feedback: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
