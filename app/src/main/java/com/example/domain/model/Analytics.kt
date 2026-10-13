package com.example.domain.model

data class HotsAnalytics(
    val studentId: String,
    val studentName: String,
    val averageQuizScore: Float,
    val practicalPblScore: Float,
    val forumParticipationScore: Float,
    val totalHotsIndex: Float,
    val category: String,
    val completedModulesCount: Int,
    val totalModulesCount: Int
)

enum class SyncStatus {
    IDLE, SYNCING, SUCCESS, FAILED
}

data class PendingSyncItem(
    val id: String,
    val entityType: String, // "JURNAL", "QUIZ_RESULT", "DISCUSSION_POST"
    val payloadJson: String,
    val timestamp: Long
)
