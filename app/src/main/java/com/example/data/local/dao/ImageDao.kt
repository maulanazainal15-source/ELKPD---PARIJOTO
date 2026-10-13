package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Query("SELECT * FROM image_notes WHERE userId = :userId AND sectionKey = :sectionKey")
    fun getImagesBySection(userId: Int, sectionKey: String): Flow<List<ImageEntity>>

    @Query("DELETE FROM image_notes WHERE id = :imageId")
    suspend fun deleteImageById(imageId: Int)
}
