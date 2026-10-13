package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BibliographyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BibliographyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: BibliographyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BibliographyEntity>)

    @Query("SELECT * FROM bibliographies ORDER BY orderIndex ASC, updatedAt DESC")
    fun getAllBibliographies(): Flow<List<BibliographyEntity>>

    @Query("SELECT * FROM bibliographies WHERE category = :category ORDER BY orderIndex ASC, updatedAt DESC")
    fun getBibliographiesByCategory(category: String): Flow<List<BibliographyEntity>>

    @Query("SELECT * FROM bibliographies WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): BibliographyEntity?

    @Query("DELETE FROM bibliographies WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM bibliographies")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM bibliographies")
    suspend fun getCount(): Int
}
