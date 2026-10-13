package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evaluation_results")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val scoreMultipleChoice: Float,
    val scoreEssay: Float,
    val scoreFinal: Float,
    val completedDate: String,
    val isSubmitted: Boolean = false,
    val pdfExportPath: String? = null
)
