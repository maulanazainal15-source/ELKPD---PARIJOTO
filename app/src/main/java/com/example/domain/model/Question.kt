package com.example.domain.model

data class Question(
    val id: String,
    val text: String,
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1,
    val category: String,
    val ethnoscienceContext: String = ""
)
