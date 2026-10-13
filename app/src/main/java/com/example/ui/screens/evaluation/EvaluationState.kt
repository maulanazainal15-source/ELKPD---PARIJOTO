package com.example.ui.screens.evaluation

data class PgQuestion(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

data class EssayQuestion(
    val id: Int,
    val questionText: String,
    val rubricText: String
)

data class EvaluationState(
    val currentPgIndex: Int = 0,
    val pgQuestions: List<PgQuestion> = emptyList(),
    val selectedPgAnswers: Map<Int, Int> = emptyMap(), // questionId -> selectedOptionIndex

    val currentEssayIndex: Int = 0,
    val essayQuestions: List<EssayQuestion> = emptyList(),
    val essayAnswers: Map<Int, String> = emptyMap(), // questionId -> answerText

    val calculatedPgScore: Double = 0.0,
    val isSubmitted: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
