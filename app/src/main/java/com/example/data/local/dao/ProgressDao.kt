package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ProgressEntity)

    @Query("SELECT * FROM activity_progress WHERE userId = :userId AND activityId = :activityId")
    fun getProgressByActivity(userId: Int, activityId: String): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM activity_progress WHERE userId = :userId")
    fun getAllProgressByUser(userId: Int): Flow<List<ProgressEntity>>

    @Query("DELETE FROM activity_progress WHERE userId = :userId")
    suspend fun clearAllProgressByUser(userId: Int)
}
