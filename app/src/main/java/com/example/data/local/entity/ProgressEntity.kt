package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_progress")
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val activityId: String, // e.g., "ACT_1", "ACT_2"
    val phaseId: String, // e.g., "PHASE_1", "PHASE_2"
    val status: String, // "NOT_STARTED", "IN_PROGRESS", "COMPLETED"
    val percentage: Int, // 0 - 100
    val lastUpdated: Long = System.currentTimeMillis()
)
