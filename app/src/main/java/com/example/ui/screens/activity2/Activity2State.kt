package com.example.ui.screens.activity2

data class ObservationRow(
    val sampel: String = "",
    val warnaAwal: String = "",
    val phPerkiraan: String = "",
    val warnaSetelahReaksi: String = "",
    val keterangan: String = ""
)

data class Activity2State(
    val currentStep: Int = 1,
    // Step 1: Teori & Prosedur Eksperimen
    val isProcedureRead: Boolean = false,
    // Step 2: Rekonstruksi Sains Masyarakat ke Ilmiah
    val indigenousNote: String = "",
    val scientificExplanation: String = "",
    // Step 3: Data Pengamatan Praktikum (Tabel Eksperimen)
    val observations: List<ObservationRow> = listOf(
        ObservationRow(sampel = "Ekstrak Parijoto + Larutan Asam (Cuka)"),
        ObservationRow(sampel = "Ekstrak Parijoto + Larutan Basa (Detergen/Kapur)"),
        ObservationRow(sampel = "Ekstrak Parijoto + Air Netral")
    ),
    // Step 4: Analisis & Pertanyaan Diskusi
    val discussionAnswer: String = "",
    // Step 5: Kesimpulan
    val conclusionAnswer: String = "",
    val progressPercentage: Int = 0,
    val isSaved: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
