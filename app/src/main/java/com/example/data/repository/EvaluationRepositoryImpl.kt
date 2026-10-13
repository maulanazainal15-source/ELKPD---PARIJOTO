package com.example.data.repository

import com.example.data.local.dao.ResultDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.EvaluationResult
import com.example.domain.repository.EvaluationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class EvaluationRepositoryImpl(
    private val resultDao: ResultDao
) : EvaluationRepository {

    override suspend fun saveEvaluationResult(result: EvaluationResult): Long {
        return resultDao.saveResult(result.toEntity())
    }

    override suspend fun insertOrUpdateResult(result: EvaluationResult): Long {
        val existing = resultDao.getResultByUser(result.userId).firstOrNull()
        val entity = result.copy(id = existing?.id ?: result.id).toEntity()
        return resultDao.saveResult(entity)
    }

    override fun getResultByUser(userId: Int): Flow<EvaluationResult?> {
        return resultDao.getResultByUser(userId).map { it?.toDomain() }
    }

    override fun getAllResults(): Flow<List<EvaluationResult>> {
        return resultDao.getAllResults().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun clearResultByUser(userId: Int) {
        resultDao.clearResultByUser(userId)
    }
}
