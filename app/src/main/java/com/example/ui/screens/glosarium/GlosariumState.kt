package com.example.ui.screens.glosarium

import com.example.domain.model.GlosariumItem

data class GlosariumState(
    val searchQuery: String = "",
    val selectedCategory: String = "Semua",
    val items: List<GlosariumItem> = emptyList(),
    val filteredItems: List<GlosariumItem> = emptyList(),
    val categories: List<String> = listOf("Semua", "Kearifan Lokal", "Kimia Organik", "Biologi")
)
