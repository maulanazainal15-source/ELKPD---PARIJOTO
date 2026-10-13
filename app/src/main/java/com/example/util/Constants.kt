package com.example.util

/**
 * Konstanta Global Aplikasi E-LKPD Etnosains Parijoto & Instrumen UAT (Phase 13)
 */
object Constants {
    const val APP_NAME = "E-LKPD Keanekaragaman Hayati: Eksplorasi Etnosains Parijoto"
    const val SUBJECT_NAME = "IPA Biologi / Kimia Terintegrasi"
    const val GRADE_PHASE = "SMA/MA Kelas X / Fase E"
    const val AUTHOR_NAME = "Ahzami, S.Pd., Gr"
    const val PUBLICATION_YEAR = 2026

    const val DATABASE_NAME = "parijoto_ethnoscience_db"
    const val ACTIVITY_1_ID = "ACT_1"
    const val ACTIVITY_2_ID = "ACT_2"

    data class UatCriterion(
        val number: Int,
        val aspect: String,
        val indicator: String
    )

    val UAT_CRITERIA_LIST = listOf(
        UatCriterion(
            number = 1,
            aspect = "Onboarding & Autentikasi",
            indicator = "Siswa dapat mengisi nama, sekolah, dan kelas dengan cepat tanpa crash."
        ),
        UatCriterion(
            number = 2,
            aspect = "E-Modul & Materi Etnosains",
            indicator = "Teks kearifan lokal Parijoto dan rumus kimia (pH, Antosianin) terbaca jelas."
        ),
        UatCriterion(
            number = 3,
            aspect = "Aktivitas 1 (PBL)",
            indicator = "Tahapan sintaks Problem-Based Learning dapat diisi dan data tersimpan otomatis."
        ),
        UatCriterion(
            number = 4,
            aspect = "Aktivitas 2 (Praktikum)",
            indicator = "Tabel pemetaan sainmas ke sains ilmiah terisi dan terverifikasi dengan benar."
        ),
        UatCriterion(
            number = 5,
            aspect = "Evaluasi HOTS",
            indicator = "Kuis Pilihan Ganda menghitung skor secara otomatis (0 - 100)."
        ),
        UatCriterion(
            number = 6,
            aspect = "Profil & Export PDF",
            indicator = "Dokumen Rapor PDF berhasil digenerate dan dapat dibuka/dibagikan."
        ),
        UatCriterion(
            number = 7,
            aspect = "Glosarium Interaktif",
            indicator = "Fitur pencarian istilah kearifan lokal merespons kata kunci dengan presisi."
        )
    )

    /**
     * Menghitung persentase kelayakan UAT: P = (totalScore / maxScore) * 100%
     */
    fun calculateUatPercentage(totalScore: Int, maxScore: Int): Double {
        if (maxScore <= 0) return 0.0
        return (totalScore.toDouble() / maxScore.toDouble()) * 100.0
    }

    /**
     * Menentukan kategori kelayakan berdasarkan persentase UAT
     */
    fun getUatFeasibilityCategory(percentage: Double): String {
        return when {
            percentage >= 81.0 -> "Sangat Layak (Siap Digunakan)"
            percentage >= 61.0 -> "Layak (Revisi Kecil)"
            percentage >= 41.0 -> "Cukup Layak"
            else -> "Tidak Layak"
        }
    }
}
