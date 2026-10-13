package com.example.domain.model

/**
 * Item pernyataan untuk kuesioner System Usability Scale (SUS) dan Evaluasi Pedagogis Etnosains.
 */
data class SurveyStatement(
    val id: Int,
    val statementText: String,
    val isPositiveStatement: Boolean = true,
    val selectedScore: Int = 3 // Skala Likert 1 (Sangat Tidak Setuju) - 5 (Sangat Setuju)
)

data class UsabilitySurveyResult(
    val susFinalScore: Double, // Rentang 0.0 - 100.0
    val gradeCategory: String, // Misal: "Acceptable / Grade A (Excellent)"
    val qualitativeFeedback: String,
    val submittedAtMillis: Long = System.currentTimeMillis()
)
