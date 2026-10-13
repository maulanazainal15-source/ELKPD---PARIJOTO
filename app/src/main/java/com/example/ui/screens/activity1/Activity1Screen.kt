package com.example.ui.screens.activity1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ui.components.LearningProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity1Screen(
    navController: NavController,
    viewModel: Activity1ViewModel = viewModel(factory = Activity1ViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aktivitas 1: Eksplorasi Etnosains",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Progress & Stepper PBL
            PblProgressHeader(
                currentPhase = state.currentPhase,
                overallProgress = state.progressPercentage,
                onPhaseSelected = { phase -> viewModel.navigateToPhase(phase) }
            )

            // Dynamic Content Berdasarkan Sintaks PBL (Fase 1-5)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                when (state.currentPhase) {
                    1 -> Phase1OrientasiContent(
                        answer = state.orientasiAnswer,
                        onAnswerChange = { viewModel.updateAnswer(1, it) }
                    )
                    2 -> Phase2OrganisasiContent(
                        answer = state.organisasiAnswer,
                        onAnswerChange = { viewModel.updateAnswer(2, it) }
                    )
                    3 -> Phase3PenyelidikanContent(
                        answer = state.penyelidikanAnswer,
                        onAnswerChange = { viewModel.updateAnswer(3, it) }
                    )
                    4 -> Phase4KaryaContent(
                        answer = state.karyaAnswer,
                        onAnswerChange = { viewModel.updateAnswer(4, it) }
                    )
                    5 -> Phase5EvaluasiContent(
                        answer = state.evaluasiAnswer,
                        onAnswerChange = { viewModel.updateAnswer(5, it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Navigation & Save Actions Bar
            PblActionBar(
                currentPhase = state.currentPhase,
                isSaved = state.isSaved,
                onSaveClick = { viewModel.saveProgress() },
                onPrevClick = { viewModel.navigateToPhase(state.currentPhase - 1) },
                onNextClick = { viewModel.navigateToPhase(state.currentPhase + 1) }
            )
        }
    }
}

@Composable
private fun PblProgressHeader(
    currentPhase: Int,
    overallProgress: Int,
    onPhaseSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            LearningProgressBar(
                progressPercentage = overallProgress,
                label = "Kelengkapan Aktivitas 1"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stepper 5 Fase PBL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    val isActive = i == currentPhase
                    val isSelected = i <= currentPhase

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary
                                else if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isActive) 2.dp else 0.dp,
                                color = if (isActive) MaterialTheme.colorScheme.secondary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onPhaseSelected(i) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = i.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) MaterialTheme.colorScheme.onPrimary
                                else if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Phase1OrientasiContent(
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Fase 1: Orientasi Siswa pada Masalah",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Etnosains Parijoto Muria",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Masyarakat lereng Gunung Muria memanfaatkan buah Parijoto secara turun-temurun untuk menjaga kesehatan tubuh dan pengobatan tradisional. Mengapa warna khas magenta kecokelatan timbul saat ekstraksi air?",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Pertanyaan Analisis:",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = "Berdasarkan narasi di atas, rumuskan masalah utama terkait kandungan senyawa kimia asli pada buah Parijoto!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            placeholder = { Text("Tuliskan rumusan masalah kelompok Anda di sini...") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun Phase2OrganisasiContent(
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Fase 2: Mengorganisasi Siswa untuk Belajar",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Rencanakan pembagian tugas kelompok dan hipotesis awal mengenai zat antioksidan/flavonoid pada Parijoto.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            placeholder = { Text("Tuliskan hipotesis dan pembagian tugas anggota kelompok...") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun Phase3PenyelidikanContent(
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Fase 3: Membimbing Penyelidikan Mandiri / Kelompok",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Kumpulkan data observasi atau literatur mengenai kadar pH, zat pewarna alami (Antosianin), dan manfaat biologis Parijoto.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            placeholder = { Text("Catat hasil pengamatan dan pengumpulan data literatur...") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun Phase4KaryaContent(
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Fase 4: Mengembangkan & Menyajikan Hasil Karya",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Susun draf laporan ringkas atau rangkuman rekonstruksi sains masyarakat ke sains ilmiah.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            placeholder = { Text("Tuliskan poin-poin presentasi/karya ilmiah kelompok...") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun Phase5EvaluasiContent(
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Fase 5: Menganalisis & Evaluasi Proses Pemecahan Masalah",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Refleksikan kendala selama proses penyelidikan dan buat kesimpulan akhir mengenai etnosains Parijoto.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            placeholder = { Text("Tuliskan kesimpulan dan refleksi pembelajaran kelompok...") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun PblActionBar(
    currentPhase: Int,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPrevClick,
                enabled = currentPhase > 1,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sebelumnya", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = onSaveClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Save,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isSaved) "Tersimpan" else "Simpan",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Button(
                onClick = onNextClick,
                enabled = currentPhase < 5,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Lanjut", color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
