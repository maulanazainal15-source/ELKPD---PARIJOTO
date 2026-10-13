package com.example.data.repository

import com.example.data.local.dao.AnswerDao
import com.example.data.local.dao.ImageDao
import com.example.data.local.dao.ProgressDao
import com.example.data.local.entity.ProgressEntity
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.ActivityProgress
import com.example.domain.model.Answer
import com.example.domain.model.ImageNote
import com.example.domain.model.LearningProgress
import com.example.domain.model.ProgressStatus
import com.example.domain.repository.LearningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class LearningRepositoryImpl(
    private val progressDao: ProgressDao,
    private val answerDao: AnswerDao,
    private val imageDao: ImageDao
) : LearningRepository {

    override suspend fun saveProgress(progress: ActivityProgress) {
        progressDao.saveProgress(progress.toEntity())
    }

    override suspend fun insertOrUpdateProgress(progress: LearningProgress) {
        val existingList = progressDao.getProgressByActivity(progress.userId, progress.activityId).firstOrNull()
        val existingId = existingList?.lastOrNull()?.id ?: progress.id
        val status = when {
            progress.isCompleted || progress.percentage >= 100 -> ProgressStatus.COMPLETED.name
            progress.percentage > 0 -> ProgressStatus.IN_PROGRESS.name
            else -> ProgressStatus.NOT_STARTED.name
        }
        progressDao.saveProgress(
            ProgressEntity(
                id = existingId,
                userId = progress.userId,
                activityId = progress.activityId,
                phaseId = "ALL_PHASES",
                status = status,
                percentage = progress.percentage,
                lastUpdated = progress.lastUpdated
            )
        )
    }

    override fun getProgressByActivity(userId: Int, activityId: String): Flow<List<ActivityProgress>> {
        return progressDao.getProgressByActivity(userId, activityId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getProgressByUserAndActivity(userId: Int, activityId: String): Flow<LearningProgress?> {
        return progressDao.getProgressByActivity(userId, activityId).map { entities ->
            val latest = entities.maxByOrNull { it.lastUpdated }
            latest?.let {
                LearningProgress(
                    id = it.id,
                    userId = it.userId,
                    activityId = it.activityId,
                    percentage = it.percentage,
                    isCompleted = it.percentage >= 100 || it.status == ProgressStatus.COMPLETED.name,
                    lastUpdated = it.lastUpdated
                )
            }
        }
    }

    override fun getAllProgressByUser(userId: Int): Flow<List<ActivityProgress>> {
        return progressDao.getAllProgressByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun clearAllProgressByUser(userId: Int) {
        progressDao.clearAllProgressByUser(userId)
        answerDao.clearAllAnswersByUser(userId)
    }

    override suspend fun saveAnswer(answer: Answer) {
        val existing = answerDao.getAnswerByQuestion(answer.userId, answer.questionId)
        val entityToSave = answer.copy(id = existing?.id ?: answer.id).toEntity()
        answerDao.saveAnswer(entityToSave)
    }

    override suspend fun getAnswerByQuestion(userId: Int, questionId: String): Answer? {
        return answerDao.getAnswerByQuestion(userId, questionId)?.toDomain()
    }

    override fun observeAnswerByQuestion(userId: Int, questionId: String): Flow<Answer?> {
        return answerDao.observeAnswerByQuestion(userId, questionId).map { it?.toDomain() }
    }

    override fun getAnswersByActivity(userId: Int, activityType: String): Flow<List<Answer>> {
        return answerDao.getAnswersByActivity(userId, activityType).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveImageNote(imageNote: ImageNote) {
        imageDao.insertImage(imageNote.toEntity())
    }

    override fun getImagesBySection(userId: Int, sectionKey: String): Flow<List<ImageNote>> {
        return imageDao.getImagesBySection(userId, sectionKey).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteImageNote(imageId: Int) {
        imageDao.deleteImageById(imageId)
    }
}
