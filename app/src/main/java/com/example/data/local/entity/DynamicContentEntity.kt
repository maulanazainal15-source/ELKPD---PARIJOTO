package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent

@Entity(tableName = "dynamic_contents")
data class DynamicContentEntity(
    @PrimaryKey
    val id: String,
    val category: String,
    val subCategory: String,
    val title: String,
    val content: String,
    val orderIndex: Int = 0,
    val author: String = "Guru / Administrator",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
) {
    fun toDomain(): DynamicContent {
        return DynamicContent(
            id = id,
            category = try {
                ContentCategory.valueOf(category)
            } catch (e: Exception) {
                ContentCategory.LEARNING_MATERIAL
            },
            subCategory = subCategory,
            title = title,
            content = content,
            orderIndex = orderIndex,
            author = author,
            lastUpdated = lastUpdated,
            isDefault = isDefault
        )
    }

    companion object {
        fun fromDomain(domain: DynamicContent): DynamicContentEntity {
            return DynamicContentEntity(
                id = domain.id,
                category = domain.category.name,
                subCategory = domain.subCategory,
                title = domain.title,
                content = domain.content,
                orderIndex = domain.orderIndex,
                author = domain.author,
                lastUpdated = domain.lastUpdated,
                isDefault = domain.isDefault
            )
        }
    }
}
