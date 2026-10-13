package com.example.domain.repository

import com.example.domain.model.StudentSubmission
import com.example.domain.model.SubmissionType
import kotlinx.coroutines.flow.Flow

interface StudentSubmissionRepository {
    fun getAllSubmissions(): Flow<List<StudentSubmission>>
    fun getSubmissionsByType(type: SubmissionType): Flow<List<StudentSubmission>>
    fun getSubmissionsByUser(userId: Int): Flow<List<StudentSubmission>>
    suspend fun getSubmissionById(id: String): StudentSubmission?
    suspend fun submitPayload(submission: StudentSubmission)
    suspend fun updateSubmissionStatus(id: String, status: String)
    suspend fun deleteSubmission(id: String)
    suspend fun getCount(): Int
}
