package com.example.domain.model

enum class StudentLearningStatus {
    COMPLETED,
    IN_PROGRESS,
    NEEDS_REMEDIAL
}

data class StudentSummary(
    val studentId: Int,
    val fullName: String,
    val schoolName: String,
    val className: String,
    val nisn: String,
    val act1Percentage: Int,
    val act2Percentage: Int,
    val overallPercentage: Int,
    val hotsScore: Double,
    val isHotsSubmitted: Boolean,
    val status: StudentLearningStatus
)

data class ClassAnalyticsSummary(
    val totalStudents: Int = 0,
    val averageHotsScore: Double = 0.0,
    val averageProgressPercentage: Double = 0.0,
    val completedCount: Int = 0,
    val inProgressCount: Int = 0,
    val remedialCount: Int = 0
)
