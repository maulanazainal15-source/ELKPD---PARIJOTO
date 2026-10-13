package com.example.domain.model

data class StudentSubmission(
    val id: String,
    val userId: Int,
    val studentName: String,
    val studentClass: String,
    val studentNis: String,
    val submissionType: SubmissionType,
    val title: String,
    val summary: String,
    val payloadJson: String,
    val score: Float? = null,
    val status: String = "TERKIRIM", // "TERKIRIM", "DIPERIKSA", "LOLOS_KKM"
    val submittedAt: Long = System.currentTimeMillis()
)

enum class SubmissionType(val displayName: String, val badgeColorName: String) {
    ACTIVITY_1("Aktivitas 1: Eksplorasi Etnosains", "Primary"),
    ACTIVITY_2("Aktivitas 2: Rekonstruksi & Praktikum", "Secondary"),
    EVALUATION_HOTS("Evaluasi HOTS Terintegrasi", "Tertiary"),
    USABILITY_SURVEY("Survei Usabilitas (SUS & NPS)", "Info")
}
