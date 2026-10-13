package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AnswerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAnswer(answer: AnswerEntity)

    @Query("SELECT * FROM answers WHERE userId = :userId AND questionId = :questionId LIMIT 1")
    suspend fun getAnswerByQuestion(userId: Int, questionId: String): AnswerEntity?

    @Query("SELECT * FROM answers WHERE userId = :userId AND questionId = :questionId LIMIT 1")
    fun observeAnswerByQuestion(userId: Int, questionId: String): Flow<AnswerEntity?>

    @Query("SELECT * FROM answers WHERE userId = :userId AND activityType = :activityType")
    fun getAnswersByActivity(userId: Int, activityType: String): Flow<List<AnswerEntity>>

    @Query("SELECT * FROM answers WHERE userId = :userId")
    suspend fun getAllAnswersByUser(userId: Int): List<AnswerEntity>

    @Query("DELETE FROM answers WHERE userId = :userId")
    suspend fun clearAllAnswersByUser(userId: Int)
}
