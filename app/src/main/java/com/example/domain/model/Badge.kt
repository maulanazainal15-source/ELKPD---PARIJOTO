package com.example.domain.model

enum class BadgeCategory {
    EXPLORATION,
    LABORATORY,
    EXCELLENCE,
    CULTURE
}

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val category: BadgeCategory,
    val isUnlocked: Boolean = false,
    val unlockedAt: String? = null,
    val requiredXp: Int = 0
)

data class UserGamificationProfile(
    val userId: Int,
    val totalXp: Int = 0,
    val currentLevel: Int = 1,
    val levelTitle: String = "Siswa Magang Etnosains",
    val unlockedBadges: List<Badge> = emptyList(),
    val nextLevelXpThreshold: Int = 100
)
