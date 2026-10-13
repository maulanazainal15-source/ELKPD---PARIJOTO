package com.example.domain.model

data class BibliographyItem(
    val id: String,
    val title: String,
    val authors: String,
    val year: String,
    val source: String, // e.g. Jurnal Riset Kimia, Kemendikbudristek
    val category: BibliographyCategory = BibliographyCategory.JURNAL,
    val doiOrUrl: String = "",
    val description: String = "",
    val orderIndex: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Menghasilkan teks sitasi standar APA Style (7th Edition)
     */
    fun toApaCitation(): String {
        val yr = if (year.isNotBlank()) " ($year). " else ". "
        val src = if (source.isNotBlank()) " $source." else ""
        val link = if (doiOrUrl.isNotBlank()) " $doiOrUrl" else ""
        return "${authors.trim()}$yr${title.trim()}.$src$link".trim()
    }
}

enum class BibliographyCategory(val displayName: String, val chipLabel: String) {
    JURNAL("Jurnal Ilmiah Bereputasi", "Jurnal"),
    BUKU("Buku & Diktat Kimia SMA", "Buku"),
    ARTIKEL("Artikel Sains Etnosains & Muria", "Artikel"),
    KURIKULUM("Panduan & Regulasi Kurikulum Merdeka", "Kurikulum")
}
