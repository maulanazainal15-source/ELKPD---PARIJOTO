package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_notes")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val sectionKey: String, // e.g., "ACT1_MORPHOLOGY", "ACT2_OBSERVATION"
    val filePath: String,
    val caption: String,
    val timestamp: Long = System.currentTimeMillis()
)
