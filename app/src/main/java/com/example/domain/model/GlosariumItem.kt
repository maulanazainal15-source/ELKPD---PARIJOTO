package com.example.domain.model

data class GlosariumItem(
    val id: Int,
    val term: String,
    val localName: String,
    val category: String, // Misal: "Kimia Organik", "Biologi", "Kearifan Lokal"
    val scientificExplanation: String,
    val localContext: String
)

typealias UserProgress = LearningProgress
