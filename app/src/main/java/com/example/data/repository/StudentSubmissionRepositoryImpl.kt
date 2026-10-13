package com.example.data.repository

import com.example.data.local.dao.StudentSubmissionDao
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.domain.model.StudentSubmission
import com.example.domain.model.SubmissionType
import com.example.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudentSubmissionRepositoryImpl(
    private val dao: StudentSubmissionDao
) : StudentSubmissionRepository {

    override fun getAllSubmissions(): Flow<List<StudentSubmission>> {
        return dao.getAllSubmissions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSubmissionsByType(type: SubmissionType): Flow<List<StudentSubmission>> {
        return dao.getSubmissionsByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSubmissionsByUser(userId: Int): Flow<List<StudentSubmission>> {
        return dao.getSubmissionsByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSubmissionById(id: String): StudentSubmission? {
        return dao.getSubmissionById(id)?.toDomain()
    }

    override suspend fun submitPayload(submission: StudentSubmission) {
        dao.insertSubmission(StudentSubmissionEntity.fromDomain(submission))
    }

    override suspend fun updateSubmissionStatus(id: String, status: String) {
        dao.updateStatus(id, status)
    }

    override suspend fun deleteSubmission(id: String) {
        dao.deleteSubmission(id)
    }

    override suspend fun getCount(): Int {
        return dao.getCount()
    }
}
