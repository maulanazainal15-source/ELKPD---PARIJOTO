package com.example.ui.screens.glosarium

import androidx.lifecycle.ViewModel
import com.example.domain.model.GlosariumItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GlosariumViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GlosariumState())
    val uiState: StateFlow<GlosariumState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val dummyGlosarium = listOf(
            GlosariumItem(
                id = 1,
                term = "Antosianin",
                localName = "Zat Warna Ungu Parijoto",
                category = "Kimia Organik",
                scientificExplanation = "Pigmen larut air kelompok flavonoid yang memberikan warna merah, ungu, hingga biru. Berfungsi sebagai antioksidan alami penangkap radikal bebas.",
                localContext = "Masyarakat Kudus menggunakan warna ungu pekat Parijoto sebagai penanda kematangan buah yang kaya khasiat."
            ),
            GlosariumItem(
                id = 2,
                term = "Maserasi",
                localName = "Penggodokan/Perendaman Tradisional",
                category = "Kimia Organik",
                scientificExplanation = "Metode ekstraksi dingin dengan merendam simplisia dalam pelarut zat cair (H2O atau etanol) untuk menarik zat aktif tanpa merusak struktur termolabil.",
                localContext = "Proses pembuatan resep jamu/sirup Parijoto secara tradisional melalui perendaman dan perebusan lambat."
            ),
            GlosariumItem(
                id = 3,
                term = "Flavonoid",
                localName = "Zat Khasiat Pembawa Kesehatan",
                category = "Kimia Organik",
                scientificExplanation = "Senyawa polifenol metabolit sekunder pada tumbuhan yang memiliki aktivitas antibakteri, antiinflamasi, dan antioksidan.",
                localContext = "Dipercaya secara ilmiah mendukung kesehatan ibu hamil dan menjaga kebugaran tubuh."
            ),
            GlosariumItem(
                id = 4,
                term = "Medinilla magnifica",
                localName = "Parijoto Gunung Muria",
                category = "Biologi",
                scientificExplanation = "Spesies tumbuhan semak epifitik dari famili Melastomataceae yang tumbuh subur di lereng Gunung Muria pada ketinggian 800–1400 mdpl.",
                localContext = "Tumbuhan khas kawasan peziarahan Sunan Muria yang dijadikan warisan budaya dan produk khas daerah Kudus."
            ),
            GlosariumItem(
                id = 5,
                term = "pH & Perubahan Warna",
                localName = "Rasa Asam dan Sifat Indikator",
                category = "Kearifan Lokal",
                scientificExplanation = "Pigmen antosianin Parijoto sensitif terhadap tingkat keasaman (pH). Berwarna merah cerah pada suasana asam (pH < 3) dan berubah menjadi kebiruan/kehijauan pada suasana basa.",
                localContext = "Penggunaan perasan buah Parijoto sebagai indikator asam-basa alami sederhana dalam praktikum laboratorium sekolah."
            )
        )

        _uiState.update {
            it.copy(
                items = dummyGlosarium,
                filteredItems = dummyGlosarium
            )
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        applyFilter()
    }

    fun onCategorySelect(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilter()
    }

    private fun applyFilter() {
        val query = _uiState.value.searchQuery.lowercase().trim()
        val category = _uiState.value.selectedCategory

        val filtered = _uiState.value.items.filter { item ->
            val matchesQuery = item.term.lowercase().contains(query) ||
                    item.localName.lowercase().contains(query) ||
                    item.scientificExplanation.lowercase().contains(query)
            val matchesCategory = category == "Semua" || item.category.equals(category, ignoreCase = true)

            matchesQuery && matchesCategory
        }

        _uiState.update { it.copy(filteredItems = filtered) }
    }
}
