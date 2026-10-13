package com.example.domain.model

enum class RecommendationPriority {
    HIGH,
    MEDIUM,
    LOW
}

data class Recommendation(
    val id: String,
    val title: String,
    val description: String,
    val targetRoute: String,
    val priority: RecommendationPriority
)
