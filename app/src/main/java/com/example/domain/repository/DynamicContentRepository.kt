package com.example.domain.repository

import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent
import kotlinx.coroutines.flow.Flow

interface DynamicContentRepository {
    fun getAllContents(): Flow<List<DynamicContent>>
    fun getContentsByCategory(category: ContentCategory): Flow<List<DynamicContent>>
    fun getContentsBySubCategory(subCategory: String): Flow<List<DynamicContent>>
    suspend fun getContentById(id: String): DynamicContent?
    suspend fun saveContent(content: DynamicContent): Result<Unit>
    suspend fun deleteContent(id: String): Result<Unit>
    suspend fun resetToDefault(): Result<Unit>
    suspend fun initializeDefaultIfEmpty()
}
