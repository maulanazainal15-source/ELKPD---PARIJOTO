package com.example.domain.model

data class EvaluationResult(
    val id: Int = 0,
    val userId: Int,
    val scoreMultipleChoice: Float = 0f,
    val scoreEssay: Float = 0f,
    val scoreFinal: Float = 0f,
    val completedDate: String = "",
    val isSubmitted: Boolean = false,
    val pdfExportPath: String? = null,
    val pgScore: Double = scoreMultipleChoice.toDouble()
) {
    constructor(
        userId: Int,
        pgScore: Double,
        isSubmitted: Boolean = true
    ) : this(
        id = 0,
        userId = userId,
        scoreMultipleChoice = pgScore.toFloat(),
        scoreEssay = 0f,
        scoreFinal = pgScore.toFloat(),
        completedDate = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
        isSubmitted = isSubmitted,
        pdfExportPath = null,
        pgScore = pgScore
    )
}
