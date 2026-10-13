package com.example.ui.screens.ar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Ar3DModelItem
import com.example.domain.model.ArNodeDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArUiState(
    val selectedModel: Ar3DModelItem? = null,
    val activeNodeDetail: ArNodeDetail? = null,
    val isArTrackingActive: Boolean = false,
    val isLoading: Boolean = false
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
                title = "Struktur Molekul Antosianin Parijoto",
                description = "Visualisasi 3D rantai flavonol dan kation flavilium pelindung warna pigmen.",
                modelGlbUrl = "models/parijoto_anthocyanin.glb",
                isAnthocyaninMolecule = true,
                interactiveNodes = listOf(
                    ArNodeDetail(
                        nodeId = "node_flavylium",
                        label = "Kation Flavilium",
                        scientificExplanation = "Penyebab warna merah cerah pada suasana pH asam (pH < 3)."
                    ),
                    ArNodeDetail(
                        nodeId = "node_hydroxyl",
                        label = "Gugus Hidroksil (-OH)",
                        scientificExplanation = "Berperan penting dalam reaksi transfer proton dan donor antioksidan."
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
