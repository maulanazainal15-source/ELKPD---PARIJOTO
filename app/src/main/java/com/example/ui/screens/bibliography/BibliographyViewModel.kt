package com.example.ui.screens.bibliography

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.model.BibliographyCategory
import com.example.domain.model.BibliographyItem
import com.example.domain.repository.BibliographyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BibliographyUiState(
    val references: List<BibliographyItem> = emptyList(),
    val filteredReferences: List<BibliographyItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: BibliographyCategory? = null,
    val isLoading: Boolean = true,
    val copiedCitationMessage: String? = null
)

class BibliographyViewModel(
    private val repository: BibliographyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BibliographyUiState())
    val uiState: StateFlow<BibliographyUiState> = _uiState.asStateFlow()

    init {
        loadBibliographies()
    }

    private fun loadBibliographies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAllBibliographies()
                .catch {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { items ->
                    _uiState.update { state ->
                        state.copy(
                            references = items,
                            filteredReferences = applyFilter(items, state.searchQuery, state.selectedCategory),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredReferences = applyFilter(state.references, query, state.selectedCategory)
            )
        }
    }

    fun onCategorySelect(category: BibliographyCategory?) {
        _uiState.update { state ->
            val newCategory = if (state.selectedCategory == category) null else category
            state.copy(
                selectedCategory = newCategory,
                filteredReferences = applyFilter(state.references, state.searchQuery, newCategory)
            )
        }
    }

    fun setCopiedMessage(message: String?) {
        _uiState.update { it.copy(copiedCitationMessage = message) }
    }

    private fun applyFilter(
        items: List<BibliographyItem>,
        query: String,
        category: BibliographyCategory?
    ): List<BibliographyItem> {
        return items.filter { item ->
            val matchesCategory = category == null || item.category == category
            val matchesQuery = query.isBlank() ||
                item.title.contains(query, ignoreCase = true) ||
                item.authors.contains(query, ignoreCase = true) ||
                item.source.contains(query, ignoreCase = true) ||
                item.year.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                BibliographyViewModel(
                    repository = RepositoryModule.provideBibliographyRepository(app)
                )
            }
        }
    }
}
