package com.example.domain.model

data class ActivityProgress(
    val id: Int = 0,
    val userId: Int,
    val activityId: String,
    val phaseId: String,
    val status: ProgressStatus,
    val percentage: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class ProgressStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}
