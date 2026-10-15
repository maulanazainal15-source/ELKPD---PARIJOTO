package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.DynamicContentDao
import com.example.data.local.entity.DynamicContentEntity
import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent
import com.example.domain.repository.DynamicContentRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DynamicContentRepositoryImpl(
    private val dynamicContentDao: DynamicContentDao,
    private val firestore: FirebaseFirestore? = null
) : DynamicContentRepository {

    private val initScope = CoroutineScope(Dispatchers.IO)
    private var snapshotListener: ListenerRegistration? = null

    init {
        initScope.launch {
            initializeDefaultIfEmpty()
            startRealtimeCloudSync()
        }
    }

    private fun startRealtimeCloudSync() {
        val fs = firestore ?: return
        try {
            snapshotListener = fs.collection("dynamic_contents")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("DynamicContentRepo", "Realtime Firestore listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        initScope.launch {
                            val items = snapshot.documents.mapNotNull { doc ->
                                try {
                                    val id = doc.getString("id") ?: doc.id
                                    val catStr = doc.getString("category") ?: "GUIDE"
                                    val category = try { ContentCategory.valueOf(catStr) } catch (e: Exception) { ContentCategory.GUIDE }
                                    val subCategory = doc.getString("subCategory") ?: "UMUM"
                                    val title = doc.getString("title") ?: ""
                                    val content = doc.getString("content") ?: ""
                                    val orderIndex = doc.getLong("orderIndex")?.toInt() ?: 0
                                    val author = doc.getString("author") ?: "Guru / Administrator"
                                    val timestamp = doc.getTimestamp("lastUpdated")?.toDate()?.time
                                        ?: System.currentTimeMillis()
                                    val isDefault = doc.getBoolean("isDefault") ?: false

                                    DynamicContentEntity(
                                        id = id,
                                        category = category.name,
                                        subCategory = subCategory,
                                        title = title,
                                        content = content,
                                        orderIndex = orderIndex,
                                        author = author,
                                        lastUpdated = timestamp,
                                        isDefault = isDefault
                                    )
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            if (items.isNotEmpty()) {
                                dynamicContentDao.insertAll(items)
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("DynamicContentRepo", "Gagal inisialisasi cloud sync: ${e.message}", e)
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

            val fs = firestore
            if (fs != null) {
                try {
                    val map = hashMapOf<String, Any>(
                        "id" to content.id,
                        "category" to content.category.name,
                        "subCategory" to content.subCategory,
                        "title" to content.title,
                        "content" to content.content,
                        "orderIndex" to content.orderIndex,
                        "author" to content.author,
                        "lastUpdated" to FieldValue.serverTimestamp(),
                        "isDefault" to content.isDefault
                    )
                    fs.collection("dynamic_contents").document(content.id)
                        .set(map, SetOptions.merge())
                        .await()
                } catch (e: Exception) {
                    Log.w("DynamicContentRepo", "Gagal simpan ke Firestore: ${e.message}")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteContent(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dynamicContentDao.deleteById(id)
            val fs = firestore
            if (fs != null) {
                try {
                    fs.collection("dynamic_contents").document(id).delete().await()
                } catch (e: Exception) {
                    Log.w("DynamicContentRepo", "Gagal hapus di Firestore: ${e.message}")
                }
            }
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

            val fs = firestore
            if (fs != null) {
                try {
                    defaults.forEach { item ->
                        val map = hashMapOf<String, Any>(
                            "id" to item.id,
                            "category" to item.category,
                            "subCategory" to item.subCategory,
                            "title" to item.title,
                            "content" to item.content,
                            "orderIndex" to item.orderIndex,
                            "author" to item.author,
                            "lastUpdated" to FieldValue.serverTimestamp(),
                            "isDefault" to item.isDefault
                        )
                        fs.collection("dynamic_contents").document(item.id).set(map, SetOptions.merge())
                    }
                } catch (e: Exception) {
                    Log.w("DynamicContentRepo", "Gagal sinkron default ke Firestore: ${e.message}")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initializeDefaultIfEmpty() = withContext(Dispatchers.IO) {
        if (dynamicContentDao.getCount() == 0) {
            val defaults = DynamicContentDefaults.getDefaultContents().map {
                DynamicContentEntity.fromDomain(it)
            }
            dynamicContentDao.insertAll(defaults)
        }
    }
}
