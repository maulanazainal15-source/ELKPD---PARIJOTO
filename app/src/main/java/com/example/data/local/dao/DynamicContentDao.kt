package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DynamicContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DynamicContentDao {

    @Query("SELECT * FROM dynamic_contents ORDER BY category ASC, orderIndex ASC, lastUpdated DESC")
    fun getAllContents(): Flow<List<DynamicContentEntity>>

    @Query("SELECT * FROM dynamic_contents WHERE category = :category ORDER BY orderIndex ASC, lastUpdated DESC")
    fun getContentsByCategory(category: String): Flow<List<DynamicContentEntity>>

    @Query("SELECT * FROM dynamic_contents WHERE subCategory = :subCategory ORDER BY orderIndex ASC")
    fun getContentsBySubCategory(subCategory: String): Flow<List<DynamicContentEntity>>

    @Query("SELECT * FROM dynamic_contents WHERE id = :id LIMIT 1")
    suspend fun getContentById(id: String): DynamicContentEntity?

    @Query("SELECT COUNT(*) FROM dynamic_contents")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(content: DynamicContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contents: List<DynamicContentEntity>)

    @Query("DELETE FROM dynamic_contents WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM dynamic_contents")
    suspend fun deleteAll()
}
