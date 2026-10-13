package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveResult(result: ResultEntity): Long

    @Query("SELECT * FROM evaluation_results WHERE userId = :userId LIMIT 1")
    fun getResultByUser(userId: Int): Flow<ResultEntity?>

    @Query("SELECT * FROM evaluation_results")
    fun getAllResults(): Flow<List<ResultEntity>>

    @Query("DELETE FROM evaluation_results WHERE userId = :userId")
    suspend fun clearResultByUser(userId: Int)
}
