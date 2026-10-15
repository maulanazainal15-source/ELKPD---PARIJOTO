package com.example.ui.screens.ar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Ar3DModelItem
import com.example.domain.model.ArNodeDetail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArUiState(
    val selectedModel: Ar3DModelItem? = null,
    val activeNodeDetail: ArNodeDetail? = null,
    val isArTrackingActive: Boolean = false,
    val isLoading: Boolean = false,
    val isModelDownloaded: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val cloudStorageUrl: String = "https://firebasestorage.googleapis.com/v0/b/parijoto-applet.appspot.com/o/models%2Fparijoto_anthocyanin.glb?alt=media"
)

class ArViewerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ArUiState())
    val uiState: StateFlow<ArUiState> = _uiState.asStateFlow()

    init {
        loadDefaultModel()
    }

    private fun loadDefaultModel() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val anthocyaninModel = Ar3DModelItem(
                id = "model_anthocyanin_3d",
                title = "Struktur Molekul Antosianin Parijoto (C₁₅H₁₁O₆⁺)",
                description = "Visualisasi 3D kation flavilium pelindung pigmen dan cincin heterosiklik pengikat hidrogen.",
                modelGlbUrl = "https://firebasestorage.googleapis.com/v0/b/parijoto-applet.appspot.com/o/models%2Fparijoto_anthocyanin.glb?alt=media",
                isAnthocyaninMolecule = true,
                interactiveNodes = listOf(
                    ArNodeDetail(
                        nodeId = "node_flavylium",
                        label = "Kation Flavilium",
                        scientificExplanation = "Pusat kromofor penyebab warna merah cerah pada suasana pH asam (pH < 3)."
                    ),
                    ArNodeDetail(
                        nodeId = "node_hydroxyl",
                        label = "Gugus Hidroksil (-OH)",
                        scientificExplanation = "Berperan penting dalam reaksi transfer proton asam-basa dan aktivitas antioksidan penangkal radikal bebas."
                    ),
                    ArNodeDetail(
                        nodeId = "node_chalcone",
                        label = "Cincin Benzenoid B",
                        scientificExplanation = "Mengalami kesetimbangan tautomerik saat pH meningkat di atas 7 menjadi warna ungu dan hijau pudar."
                    )
                )
            )

            _uiState.update {
                it.copy(
                    selectedModel = anthocyaninModel,
                    isLoading = false
                )
            }
        }
    }

    fun downloadModelAsset() {
        if (_uiState.value.isDownloading || _uiState.value.isModelDownloaded) return

        _uiState.update { it.copy(isDownloading = true, downloadProgress = 0.1f) }
        viewModelScope.launch {
            for (step in 2..10) {
                delay(120L)
                _uiState.update { it.copy(downloadProgress = step / 10f) }
            }
            _uiState.update {
                it.copy(
                    isDownloading = false,
                    isModelDownloaded = true,
                    downloadProgress = 1f
                )
            }
        }
    }

    fun onNodeClicked(node: ArNodeDetail) {
        _uiState.update { it.copy(activeNodeDetail = node) }
    }

    fun dismissNodeDetail() {
        _uiState.update { it.copy(activeNodeDetail = null) }
    }

    fun setArTrackingStatus(isActive: Boolean) {
        _uiState.update { it.copy(isArTrackingActive = isActive) }
    }
}
