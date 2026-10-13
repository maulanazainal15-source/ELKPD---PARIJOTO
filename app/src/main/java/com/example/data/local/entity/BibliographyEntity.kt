package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.BibliographyCategory
import com.example.domain.model.BibliographyItem

@Entity(tableName = "bibliographies")
data class BibliographyEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val authors: String,
    val year: String,
    val source: String,
    val category: String,
    val doiOrUrl: String = "",
    val description: String = "",
    val orderIndex: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): BibliographyItem {
        val cat = try {
            BibliographyCategory.valueOf(category)
        } catch (e: Exception) {
            BibliographyCategory.JURNAL
        }
        return BibliographyItem(
            id = id,
            title = title,
            authors = authors,
            year = year,
            source = source,
            category = cat,
            doiOrUrl = doiOrUrl,
            description = description,
            orderIndex = orderIndex,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: BibliographyItem): BibliographyEntity {
            return BibliographyEntity(
                id = domain.id,
                title = domain.title,
                authors = domain.authors,
                year = domain.year,
                source = domain.source,
                category = domain.category.name,
                doiOrUrl = domain.doiOrUrl,
                description = domain.description,
                orderIndex = domain.orderIndex,
                updatedAt = domain.updatedAt
            )
        }
    }
}
