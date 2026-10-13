package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.StudentSubmission
import com.example.domain.model.SubmissionType

@Entity(tableName = "student_submissions")
data class StudentSubmissionEntity(
    @PrimaryKey
    val id: String,
    val userId: Int,
    val studentName: String,
    val studentClass: String,
    val studentNis: String,
    val submissionType: String,
    val title: String,
    val summary: String,
    val payloadJson: String,
    val score: Float? = null,
    val status: String = "TERKIRIM",
    val submittedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): StudentSubmission {
        val type = try {
            SubmissionType.valueOf(submissionType)
        } catch (e: Exception) {
            SubmissionType.ACTIVITY_1
        }
        return StudentSubmission(
            id = id,
            userId = userId,
            studentName = studentName,
            studentClass = studentClass,
            studentNis = studentNis,
            submissionType = type,
            title = title,
            summary = summary,
            payloadJson = payloadJson,
            score = score,
            status = status,
            submittedAt = submittedAt
        )
    }

    companion object {
        fun fromDomain(domain: StudentSubmission): StudentSubmissionEntity {
            return StudentSubmissionEntity(
                id = domain.id,
                userId = domain.userId,
                studentName = domain.studentName,
                studentClass = domain.studentClass,
                studentNis = domain.studentNis,
                submissionType = domain.submissionType.name,
                title = domain.title,
                summary = domain.summary,
                payloadJson = domain.payloadJson,
                score = domain.score,
                status = domain.status,
                submittedAt = domain.submittedAt
            )
        }
    }
}
