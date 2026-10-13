package com.example.domain.model

enum class ContentCategory(
    val title: String,
    val badge: String,
    val description: String
) {
    GUIDE(
        title = "Panduan & Petunjuk",
        badge = "Panduan",
        description = "Panduan penggunaan aplikasi, K3 laboratorium, dan fitur interaktif"
    ),
    CURRICULUM(
        title = "Kurikulum (CP & TP)",
        badge = "Kurikulum",
        description = "Capaian Pembelajaran (CP), Tujuan Pembelajaran (TP), dan rubrik HOTS"
    ),
    LEARNING_MATERIAL(
        title = "Materi & Praktikum",
        badge = "Materi",
        description = "Modul etnosains Parijoto, sintaks PBL, dan petunjuk langkah kerja praktikum"
    );

    fun getCommonSubCategories(): List<Pair<String, String>> {
        return when (this) {
            GUIDE -> listOf(
                "PANDUAN_SISWA_GURU" to "Panduan Penggunaan Siswa & Guru",
                "KESELAMATAN_K3" to "Petunjuk Keselamatan Kerja Laboratorium (K3)",
                "FITUR_INTERAKTIF" to "Petunjuk Fitur Interaktif (AR, Quiz Duel, Forum)"
            )
            CURRICULUM -> listOf(
                "CP_PEMAHAMAN" to "CP - Elemen Pemahaman Sains",
                "CP_KETERAMPILAN" to "CP - Elemen Keterampilan Proses",
                "TUJUAN_PEMBELAJARAN" to "Tujuan Pembelajaran (TP)",
                "INDIKATOR_HOTS" to "Indikator HOTS & Rubrik Penilaian"
            )
            LEARNING_MATERIAL -> listOf(
                "MODUL_ETNOSAINS" to "Modul Etnosains Parijoto & Biokimia",
                "SINTAKS_PBL" to "Tahapan Sintaks Pembelajaran PBL",
                "PETUNJUK_PRAKTIKUM" to "Petunjuk Langkah Kerja Praktikum"
            )
        }
    }
}
