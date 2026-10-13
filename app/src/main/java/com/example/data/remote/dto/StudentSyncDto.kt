package com.example.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentSyncDto(
    @SerialName("student_id") val studentId: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("school_name") val schoolName: String,
    @SerialName("class_name") val className: String,
    @SerialName("act1_percentage") val act1Percentage: Int,
    @SerialName("act2_percentage") val act2Percentage: Int,
    @SerialName("hots_score") val hotsScore: Double,
    @SerialName("last_synced_at") val lastSyncedAt: String
)
