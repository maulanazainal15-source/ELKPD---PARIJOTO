package com.example.ui.screens.activity2

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
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun Activity2Screen(
    navController: NavController,
    viewModel: Activity2ViewModel = viewModel(factory = Activity2ViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aktivitas 2: Praktikum & Rekonstruksi Sains",
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
            // Header Progress & Stepper Aktivitas 2
            Activity2ProgressHeader(
                currentStep = state.currentStep,
                overallProgress = state.progressPercentage,
                onStepSelected = { step -> viewModel.navigateToStep(step) }
            )

            // Dynamic Content Berdasarkan Step Praktikum (Step 1-5)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                when (state.currentStep) {
                    1 -> Step1ProcedureContent(
                        isRead = state.isProcedureRead,
                        onReadChange = { viewModel.updateProcedureReadStatus(it) }
                    )
                    2 -> Step2ReconstructionContent(
                        indigenousNote = state.indigenousNote,
                        scientificExplanation = state.scientificExplanation,
                        onReconstructionChange = { indigenous, scientific ->
                            viewModel.updateReconstruction(indigenous, scientific)
                        }
                    )
                    3 -> Step3ObservationTableContent(
                        observations = state.observations,
                        onObservationRowChange = { index, row ->
                            viewModel.updateObservationRow(index, row)
                        }
                    )
                    4 -> Step4DiscussionContent(
                        answer = state.discussionAnswer,
                        onAnswerChange = { viewModel.updateDiscussionAnswer(it) }
                    )
                    5 -> Step5ConclusionContent(
                        answer = state.conclusionAnswer,
                        onAnswerChange = { viewModel.updateConclusionAnswer(it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Navigation & Save Actions Bar
            Activity2ActionBar(
                currentStep = state.currentStep,
                isSaved = state.isSaved,
                onSaveClick = { viewModel.saveProgress() },
                onPrevClick = { viewModel.navigateToStep(state.currentStep - 1) },
                onNextClick = { viewModel.navigateToStep(state.currentStep + 1) }
            )
        }
    }
}

@Composable
private fun Activity2ProgressHeader(
    currentStep: Int,
    overallProgress: Int,
    onStepSelected: (Int) -> Unit
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
                label = "Kelengkapan Aktivitas 2"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stepper 5 Tahapan Praktikum
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    val isActive = i == currentStep
                    val isSelected = i <= currentStep

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
                            .clickable { onStepSelected(i) },
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
private fun Step1ProcedureContent(
    isRead: Boolean,
    onReadChange: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "Tahap 1: Prosedur & Landasan Praktikum Etnosains",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Biotech,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Uji Sifat Asam-Basa Ekstrak Buah Parijoto",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Buah Parijoto (Medinilla magnifica) mengandung pigmen Antosianin alami yang bersifat peka terhadap perubahan tingkat keasaman (pH). Dalam suasana asam, antosianin menghasilkan warna merah/magenta terang, sedangkan dalam suasana basa warnanya berubah menjadi kecokelatan/kebiruan.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Langkah-Langkah Eksperimen:",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(6.dp))

        val steps = listOf(
            "1. Tumbuk 5-10 butir buah Parijoto matang dan tambahkan 20 ml air matang.",
            "2. Saring ekstrak untuk mendapatkan cairan filtrat berwarna magenta murni.",
            "3. Siapkan 3 tabung reaksi/wadah netral (A: Cuka/Asam, B: Sabun/Basa, C: Air Netral).",
            "4. Teteskan 5 tetes ekstrak Parijoto ke masing-masing wadah dan amati perubahan warna."
        )

        steps.forEach { stepText ->
            Text(
                text = stepText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable { onReadChange(!isRead) }
                .padding(8.dp)
        ) {
            Checkbox(
                checked = isRead,
                onCheckedChange = { onReadChange(it) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Saya dan kelompok telah membaca dan memahami prosedur praktikum di atas.",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
private fun Step2ReconstructionContent(
    indigenousNote: String,
    scientificExplanation: String,
    onReconstructionChange: (String, String) -> Unit
) {
    Column {
        Text(
            text = "Tahap 2: Rekonstruksi Pengetahuan Asli ke Sains Ilmiah",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bandingkan kepercayaan/persepsi tradisional masyarakat Muria dengan penjelasan ilmiah biokimia.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "1. Pengetahuan Asli Masyarakat (Indigenous Science):",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        OutlinedTextField(
            value = indigenousNote,
            onValueChange = { onReconstructionChange(it, scientificExplanation) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            placeholder = { Text("Contoh: Mitos bahwa air rebusan Parijoto dapat menguatkan janin atau memberi warna kulit bayi...") },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "2. Penjelasan Sains Ilmiah (Scientific Science):",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        OutlinedTextField(
            value = scientificExplanation,
            onValueChange = { onReconstructionChange(indigenousNote, it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            placeholder = { Text("Uraikan kandungan flavonoid, antioksidan, serta efek biologis secara ilmiah...") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun Step3ObservationTableContent(
    observations: List<ObservationRow>,
    onObservationRowChange: (Int, ObservationRow) -> Unit
) {
    Column {
        Text(
            text = "Tahap 3: Lembar Pengamatan Data Praktikum",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Isi data perubahan warna ekstrak Parijoto setelah ditetesi larutan uji.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        observations.forEachIndexed { index, row ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Sampel ${index + 1}: ${row.sampel}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = row.warnaAwal,
                            onValueChange = { onObservationRowChange(index, row.copy(warnaAwal = it)) },
                            label = { Text("Warna Awal") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = row.warnaSetelahReaksi,
                            onValueChange = { onObservationRowChange(index, row.copy(warnaSetelahReaksi = it)) },
                            label = { Text("Warna Reaksi") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = row.phPerkiraan,
                        onValueChange = { onObservationRowChange(index, row.copy(phPerkiraan = it)) },
                        label = { Text("Perkiraan Sifat (Asam / Basa / Netral)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
private fun Step4DiscussionContent(
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Tahap 4: Analisis & Pertanyaan Diskusi",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Mengapa warna ekstrak Parijoto berubah saat dicampur larutan asam dan basa? Jelaskan mekanisme perubahan struktur kimia zat antosianin!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("Tuliskan hasil diskusi kelompok Anda secara detail di sini...") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun Step5ConclusionContent(
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Tahap 5: Kesimpulan Praktikum & Etnosains",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Buatlah kesimpulan akhir mengenai potensi ekstrak Parijoto sebagai indikator asam-basa alami dan manfaatnya dalam sains sehari-hari.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("Tuliskan kesimpulan akhir pembelajaran praktikum di sini...") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun Activity2ActionBar(
    currentStep: Int,
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
                enabled = currentStep > 1,
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
                enabled = currentStep < 5,
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
