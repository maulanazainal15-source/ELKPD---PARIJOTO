package com.example.domain.repository

import com.example.domain.model.ActivityProgress
import com.example.domain.model.Answer
import com.example.domain.model.ImageNote
import com.example.domain.model.LearningProgress
import kotlinx.coroutines.flow.Flow

interface LearningRepository {
    // Progress
    suspend fun saveProgress(progress: ActivityProgress)
    suspend fun insertOrUpdateProgress(progress: LearningProgress)
    fun getProgressByActivity(userId: Int, activityId: String): Flow<List<ActivityProgress>>
    fun getProgressByUserAndActivity(userId: Int, activityId: String): Flow<LearningProgress?>
    fun getAllProgressByUser(userId: Int): Flow<List<ActivityProgress>>
    suspend fun clearAllProgressByUser(userId: Int)

    // Answers
    suspend fun saveAnswer(answer: Answer)
    suspend fun getAnswerByQuestion(userId: Int, questionId: String): Answer?
    fun observeAnswerByQuestion(userId: Int, questionId: String): Flow<Answer?>
    fun getAnswersByActivity(userId: Int, activityType: String): Flow<List<Answer>>

    // Images
    suspend fun saveImageNote(imageNote: ImageNote)
    fun getImagesBySection(userId: Int, sectionKey: String): Flow<List<ImageNote>>
    suspend fun deleteImageNote(imageId: Int)
}
