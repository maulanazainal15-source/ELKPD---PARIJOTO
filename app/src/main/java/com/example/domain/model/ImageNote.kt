package com.example.domain.model

data class ImageNote(
    val id: Int = 0,
    val userId: Int,
    val sectionKey: String,
    val filePath: String,
    val caption: String,
    val timestamp: Long = System.currentTimeMillis()
)
