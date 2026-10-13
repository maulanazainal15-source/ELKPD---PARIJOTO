package com.example.domain.repository

import com.example.domain.model.BibliographyCategory
import com.example.domain.model.BibliographyItem
import kotlinx.coroutines.flow.Flow

interface BibliographyRepository {
    fun getAllBibliographies(): Flow<List<BibliographyItem>>
    fun getBibliographiesByCategory(category: BibliographyCategory): Flow<List<BibliographyItem>>
    suspend fun getBibliographyById(id: String): BibliographyItem?
    suspend fun saveBibliography(item: BibliographyItem)
    suspend fun deleteBibliography(id: String)
    suspend fun resetToDefaultReferences()
}
