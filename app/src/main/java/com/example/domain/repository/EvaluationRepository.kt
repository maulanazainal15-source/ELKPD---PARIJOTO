package com.example.domain.repository

import com.example.domain.model.EvaluationResult
import kotlinx.coroutines.flow.Flow

interface EvaluationRepository {
    suspend fun saveEvaluationResult(result: EvaluationResult): Long
    suspend fun insertOrUpdateResult(result: EvaluationResult): Long
    fun getResultByUser(userId: Int): Flow<EvaluationResult?>
    fun getAllResults(): Flow<List<EvaluationResult>>
    suspend fun clearResultByUser(userId: Int)
}
