package com.example.domain.model

data class DynamicContent(
    val id: String,
    val category: ContentCategory,
    val subCategory: String,
    val title: String,
    val content: String,
    val orderIndex: Int = 0,
    val author: String = "Guru / Administrator",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
) {
    fun getSubCategoryLabel(): String {
        return when (subCategory) {
            "PANDUAN_SISWA_GURU" -> "Panduan Siswa & Guru"
            "KESELAMATAN_K3" -> "Keselamatan Kerja (K3)"
            "FITUR_INTERAKTIF" -> "Fitur Interaktif (AR, Quiz, Forum)"
            "CP_PEMAHAMAN" -> "CP Pemahaman Sains"
            "CP_KETERAMPILAN" -> "CP Keterampilan Proses"
            "TUJUAN_PEMBELAJARAN" -> "Tujuan Pembelajaran (TP)"
            "INDIKATOR_HOTS" -> "Indikator HOTS & Rubrik"
            "MODUL_ETNOSAINS" -> "Modul Etnosains"
            "SINTAKS_PBL" -> "Sintaks PBL"
            "PETUNJUK_PRAKTIKUM" -> "Petunjuk Praktikum"
            else -> subCategory.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}
