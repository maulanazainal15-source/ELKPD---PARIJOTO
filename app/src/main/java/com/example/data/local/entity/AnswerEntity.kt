package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answers")
data class AnswerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val questionId: String, // e.g., "ACT1_ORIENT_1", "ACT1_HYPO_1"
    val activityType: String, // "ACTIVITY_1", "ACTIVITY_2", "EVAL_ESSAY"
    val answerText: String,
    val score: Float = 0.0f,
    val feedback: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
