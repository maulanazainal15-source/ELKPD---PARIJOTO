package com.example.repository

import com.example.database.ElpkdDao
import com.example.model.AnswerEntity
import com.example.model.ImageEntity
import com.example.model.ProgressEntity
import com.example.model.ResultEntity
import com.example.model.TeacherConfigEntity
import com.example.model.UserEntity
import kotlinx.coroutines.flow.Flow

class ElpkdRepository(private val dao: ElpkdDao) {

    val activeUser: Flow<UserEntity?> = dao.getActiveUser()
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val allProgress: Flow<List<ProgressEntity>> = dao.getAllProgress()
    val allAnswers: Flow<List<AnswerEntity>> = dao.getAllAnswers()
    val allResults: Flow<List<ResultEntity>> = dao.getAllResults()
    val teacherConfig: Flow<TeacherConfigEntity?> = dao.getTeacherConfig()

    fun getProgressForUser(userId: Int): Flow<List<ProgressEntity>> =
        dao.getProgressForUser(userId)

    fun getAnswersForUser(userId: Int): Flow<List<AnswerEntity>> =
        dao.getAnswersForUser(userId)

    fun getImagesForUser(userId: Int): Flow<List<ImageEntity>> =
        dao.getImagesForUser(userId)

    fun getResultsForUser(userId: Int): Flow<List<ResultEntity>> =
        dao.getResultsForUser(userId)

    suspend fun saveActiveUser(user: UserEntity): Int {
        dao.deactivateAllUsers()
        val id = dao.insertUser(user.copy(isActive = true)).toInt()
        return if (user.id != 0) user.id else id
    }

    suspend fun switchActiveUser(user: UserEntity) {
        dao.deactivateAllUsers()
        dao.updateUser(user.copy(isActive = true))
    }

    suspend fun upsertProgress(progress: ProgressEntity) {
        dao.upsertProgress(progress)
    }

    suspend fun upsertAnswer(answer: AnswerEntity) {
        dao.upsertAnswer(answer)
    }

    suspend fun markAllAnswersSynced(userId: Int) {
        dao.markAllAnswersSynced(userId)
    }

    suspend fun insertImage(image: ImageEntity): Long {
        return dao.insertImage(image)
    }

    suspend fun deleteImageById(imageId: Int) {
        dao.deleteImageById(imageId)
    }

    suspend fun insertResult(result: ResultEntity): Long {
        return dao.insertResult(result)
    }

    suspend fun updateResult(result: ResultEntity) {
        dao.updateResult(result)
    }

    suspend fun upsertTeacherConfig(config: TeacherConfigEntity) {
        dao.upsertTeacherConfig(config)
    }
}
