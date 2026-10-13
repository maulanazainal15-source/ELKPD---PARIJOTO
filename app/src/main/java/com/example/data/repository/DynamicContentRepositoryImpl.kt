package com.example.data.repository

import com.example.data.local.dao.DynamicContentDao
import com.example.data.local.entity.DynamicContentEntity
import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent
import com.example.domain.repository.DynamicContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DynamicContentRepositoryImpl(
    private val dynamicContentDao: DynamicContentDao
) : DynamicContentRepository {

    private val initScope = CoroutineScope(Dispatchers.IO)

    init {
        initScope.launch {
            initializeDefaultIfEmpty()
        }
    }

    override fun getAllContents(): Flow<List<DynamicContent>> {
        return dynamicContentDao.getAllContents()
            .onStart {
                initializeDefaultIfEmpty()
            }
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override fun getContentsByCategory(category: ContentCategory): Flow<List<DynamicContent>> {
        return dynamicContentDao.getContentsByCategory(category.name)
            .onStart {
                initializeDefaultIfEmpty()
            }
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override fun getContentsBySubCategory(subCategory: String): Flow<List<DynamicContent>> {
        return dynamicContentDao.getContentsBySubCategory(subCategory)
            .onStart {
                initializeDefaultIfEmpty()
            }
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun getContentById(id: String): DynamicContent? = withContext(Dispatchers.IO) {
        dynamicContentDao.getContentById(id)?.toDomain()
    }

    override suspend fun saveContent(content: DynamicContent): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (content.title.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Judul konten tidak boleh kosong!"))
            }
            if (content.content.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Teks isi konten tidak boleh kosong!"))
            }
            val entity = DynamicContentEntity.fromDomain(content.copy(lastUpdated = System.currentTimeMillis()))
            dynamicContentDao.insertOrUpdate(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteContent(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dynamicContentDao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetToDefault(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dynamicContentDao.deleteAll()
            val defaults = DynamicContentDefaults.getDefaultContents().map {
                DynamicContentEntity.fromDomain(it)
            }
            dynamicContentDao.insertAll(defaults)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initializeDefaultIfEmpty() = withContext(Dispatchers.IO) {
        try {
            val count = dynamicContentDao.getCount()
            if (count == 0) {
                val defaults = DynamicContentDefaults.getDefaultContents().map {
                    DynamicContentEntity.fromDomain(it)
                }
                dynamicContentDao.insertAll(defaults)
            }
        } catch (e: Exception) {
            // Log or fallback
        }
    }
}
