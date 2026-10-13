package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.di.RepositoryModule
import com.example.domain.ElpkdCurriculumData
import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent
import com.example.domain.repository.DynamicContentRepository
import com.example.model.AnswerEntity
import com.example.model.TeacherConfigEntity
import com.example.ui.components.AutosaveAnswerCard
import com.example.ui.components.DocumentaryVideoDialog
import com.example.ui.components.PblPhaseIndicatorBanner
import com.example.ui.components.QrScannerAndReferenceDialog

/**
 * SUB-TAB 1: PENDAHULUAN & PETUNJUK PENGGUNAAN E-LKPD
 */
@Composable
fun PendahuluanSection(
    config: TeacherConfigEntity,
    onCompleteAndStart: () -> Unit,
    dynamicContentRepository: DynamicContentRepository = RepositoryModule.provideDynamicContentRepository(LocalContext.current)
) {
    val dynamicGuides by remember(dynamicContentRepository) {
        dynamicContentRepository.getContentsByCategory(ContentCategory.GUIDE)
    }.collectAsState(initial = emptyList())

    var selectedSubCatFilter by remember { mutableStateOf<String?>(null) }

    val filteredGuides = if (selectedSubCatFilter == null) {
        dynamicGuides
    } else {
        dynamicGuides.filter { it.subCategory == selectedSubCatFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Identitas E-LKPD Lengkap sesuai Bagian A
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "IDENTITAS RESMI E-LKPD",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                IdentityMetaRow("Mata Pelajaran", "IPA Biologi & Kimia Etnosains")
                IdentityMetaRow("Materi Pokok", "Keanekaragaman Hayati & Asam-Basa")
                IdentityMetaRow("Submateri", "Eksplorasi Etnosains Parijoto (Medinilla speciosa)")
                IdentityMetaRow("Kelas / Fase", config.classPhase)
                IdentityMetaRow("Alokasi Waktu", "2 JP (2 x 45 Menit)")
                IdentityMetaRow("Judul E-LKPD", config.moduleTitle)
                IdentityMetaRow("Penulis", config.authorName)
                IdentityMetaRow("Tahun", config.moduleYear)
                IdentityMetaRow("Satuan Pendidikan", config.schoolName)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header Petunjuk & Filter Subkategori Panduan
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Petunjuk & Panduan E-LKPD",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (dynamicGuides.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "Dinamis CMS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Sub-Panduan
                if (dynamicGuides.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedSubCatFilter == null,
                            onClick = { selectedSubCatFilter = null },
                            label = { Text("Semua", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        FilterChip(
                            selected = selectedSubCatFilter == "PANDUAN_SISWA_GURU",
                            onClick = { selectedSubCatFilter = "PANDUAN_SISWA_GURU" },
                            label = { Text("Penggunaan", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = selectedSubCatFilter == "KESELAMATAN_K3",
                            onClick = { selectedSubCatFilter = "KESELAMATAN_K3" },
                            label = { Text("K3 Lab", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = selectedSubCatFilter == "FITUR_INTERAKTIF",
                            onClick = { selectedSubCatFilter = "FITUR_INTERAKTIF" },
                            label = { Text("Fitur Interaktif", fontSize = 11.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (filteredGuides.isNotEmpty()) {
                    filteredGuides.forEach { guide ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = guide.getSubCategoryLabel(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = "Oleh: ${guide.author}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = guide.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                val lines = guide.content.lines().filter { it.isNotBlank() }
                                lines.forEach { line ->
                                    Text(
                                        text = line.trim(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Fallback Petunjuk Bawaan
                    ElpkdCurriculumData.petunjukPenggunaan.forEachIndexed { index, instruction ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = instruction,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onCompleteAndStart,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_mulai_elkpd")
                ) {
                    Text(
                        text = "Mulai E-LKPD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun IdentityMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.width(130.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * SUB-TAB 2: CAPAIAN PEMBELAJARAN (CP) & TUJUAN PEMBELAJARAN (TP) DINAMIS
 */
@Composable
fun CpAndTpSection(
    onCompleteAndContinue: () -> Unit,
    dynamicContentRepository: DynamicContentRepository = RepositoryModule.provideDynamicContentRepository(LocalContext.current)
) {
    val curriculumContents by remember(dynamicContentRepository) {
        dynamicContentRepository.getContentsByCategory(ContentCategory.CURRICULUM)
    }.collectAsState(initial = emptyList())

    val cpPemahaman = curriculumContents.firstOrNull { it.subCategory == "CP_PEMAHAMAN" }?.content
        ?: ElpkdCurriculumData.CAPAIAN_PEMBELAJARAN_PEMAHAMAN
    val cpKeterampilan = curriculumContents.firstOrNull { it.subCategory == "CP_KETERAMPILAN" }?.content
        ?: ElpkdCurriculumData.CAPAIAN_PEMBELAJARAN_KETERAMPILAN
    val tpContents = curriculumContents.filter { it.subCategory == "TUJUAN_PEMBELAJARAN" }
    val hotsIndicators = curriculumContents.firstOrNull { it.subCategory == "INDIKATOR_HOTS" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // CAPAIAN PEMBELAJARAN (Bagian D)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Capaian Pembelajaran (CP) Fase E",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (curriculumContents.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Tersinkron CMS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "1. Elemen Pemahaman Sains & Biokimia",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cpPemahaman,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "2. Elemen Keterampilan Proses",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cpKeterampilan,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TUJUAN PEMBELAJARAN (TP)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (tpContents.isNotEmpty()) "${tpContents.size} Tujuan Pembelajaran (TP)" else "6 Tujuan Pembelajaran (TP)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (tpContents.isNotEmpty()) {
                    tpContents.forEachIndexed { idx, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "TP ${idx + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                } else {
                    ElpkdCurriculumData.tujuanPembelajaran.forEachIndexed { idx, tp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "TP ${idx + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = tp,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // INDIKATOR HOTS & RUBRIK CARD (JIKA ADA DARI CMS)
        if (hotsIndicators != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = hotsIndicators.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = hotsIndicators.content,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onCompleteAndContinue,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_lanjut_aktivitas_1")
        ) {
            Text(
                text = "Pahami CP & TP — Lanjut ke Aktivitas 1",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

/**
 * SUB-TAB 3: AKTIVITAS 1 — DINAMIKA ETNOSAINS & ANALISIS MASALAH
 * Mencakup Fase 1 PBL (Orientasi Masalah) & Fase 2 PBL (Pengorganisasian Penyelidikan + Hipotesis)
 */
@Composable
fun Aktivitas1Section(
    config: TeacherConfigEntity,
    answersMap: Map<String, AnswerEntity>,
    onSaveAnswer: (String, String) -> Unit,
    onRequestAiFeedback: (String, String, String) -> Unit,
    onContinueToAktivitas2: () -> Unit,
    dynamicContentRepository: DynamicContentRepository = RepositoryModule.provideDynamicContentRepository(LocalContext.current)
) {
    var showVideoModal by remember { mutableStateOf(false) }
    var showQrModal by remember { mutableStateOf(false) }

    val dynamicMaterials by remember(dynamicContentRepository) {
        dynamicContentRepository.getContentsByCategory(ContentCategory.LEARNING_MATERIAL)
    }.collectAsState(initial = emptyList())

    val dynamicStimulus = dynamicMaterials.firstOrNull { it.id == "modul_parijoto_muria" || it.subCategory == "MODUL_ETNOSAINS" }
    val stimulusTitle = dynamicStimulus?.title ?: ElpkdCurriculumData.STIMULUS_AKTIVITAS_1_JUDUL
    val stimulusParagraphs = dynamicStimulus?.content?.split("\n\n")?.map { it.trim() }?.filter { it.isNotBlank() }
        ?: ElpkdCurriculumData.stimulusAktivitas1Paragraphs

    val savedHypothesis = answersMap["akt1_k3_hipotesis"]?.answer ?: ""
    var tindakanInput by remember { mutableStateOf("") }
    var dampakInput by remember { mutableStateOf("") }
    var customHypothesis by remember(savedHypothesis) {
        mutableStateOf(
            savedHypothesis.ifBlank {
                "Jika masyarakat melakukan budidaya mandiri (ex-situ) Parijoto di pekarangan menggunakan naungan paranet dan media humus organik, maka kebutuhan ekonomi UMKM tetap terpenuhi secara berkelanjutan tanpa merusak populasi liar di hutan lindung Gunung Muria."
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Indikator Fase 1 PBL
        PblPhaseIndicatorBanner(activePhaseNumber = 1)

        Spacer(modifier = Modifier.height(14.dp))

        // STIMULUS WACANA & MEDIA INTERAKTIF (Bagian I)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "AKTIVITAS 1 • DINAMIKA ETNOSAINS & ANALISIS MASALAH",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stimulusTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                stimulusParagraphs.forEach { paragraph ->
                    Text(
                        text = paragraph,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3 Tombol Media Sesuai Bagian I: [Putar Video] [Scan QR] [Baca Referensi]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showVideoModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_putar_video_akt1")
                    ) {
                        Icon(Icons.Default.PlayCircleFilled, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Putar Video", style = MaterialTheme.typography.labelMedium)
                    }
                    FilledTonalButton(
                        onClick = { showQrModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_scan_qr_akt1")
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan QR", style = MaterialTheme.typography.labelMedium)
                    }
                    FilledTonalButton(
                        onClick = { showQrModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_baca_referensi_akt1")
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Referensi", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // KEGIATAN 1 AKTIVITAS 1 (Bagian J)
        Text(
            text = "KEGIATAN 1 — Identifikasi & Analisis Masalah Ekologis",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val q1Text = "1. Bagaimana kaitan antara meningkatnya nilai ekonomi buah Parijoto dengan penurunan populasinya di habitat alami?"
        AutosaveAnswerCard(
            questionId = "akt1_k1_q1",
            numberLabel = "Kegiatan 1 • Pertanyaan 1",
            questionText = q1Text,
            savedAnswer = answersMap["akt1_k1_q1"]?.answer ?: "",
            aiFeedback = answersMap["akt1_k1_q1"]?.aiFeedback ?: "",
            isAiEnabled = config.isAiEnabled,
            onSaveAnswer = { onSaveAnswer("akt1_k1_q1", it) },
            onRequestAiFeedback = {
                onRequestAiFeedback("akt1_k1_q1", q1Text, "Analisis dampak ekonomi terhadap populasi habitat alami")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        val q2Text = "2. Apa konsekuensi ekologis yang mungkin terjadi bagi flora dan fauna lain di Muria jika Parijoto mengalami kepunahan lokal?"
        AutosaveAnswerCard(
            questionId = "akt1_k1_q2",
            numberLabel = "Kegiatan 1 • Pertanyaan 2",
            questionText = q2Text,
            savedAnswer = answersMap["akt1_k1_q2"]?.answer ?: "",
            aiFeedback = answersMap["akt1_k1_q2"]?.aiFeedback ?: "",
            isAiEnabled = config.isAiEnabled,
            onSaveAnswer = { onSaveAnswer("akt1_k1_q2", it) },
            onRequestAiFeedback = {
                onRequestAiFeedback("akt1_k1_q2", q2Text, "Analisis rantai makanan dan interaksi antarkomponen ekosistem Muria")
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // INDIKATOR FASE 2 PBL
        PblPhaseIndicatorBanner(activePhaseNumber = 2)

        Spacer(modifier = Modifier.height(14.dp))

        // KEGIATAN 2 AKTIVITAS 1 (Bagian K)
        Text(
            text = "KEGIATAN 2 — Analisis Awal Etnosains & Literasi Konservasi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val k2q1Text = "1. Apa yang dimaksud dengan pengetahuan lokal/etnosains terkait Parijoto?"
        AutosaveAnswerCard(
            questionId = "akt1_k2_q1",
            numberLabel = "Kegiatan 2 • Pertanyaan 1",
            questionText = k2q1Text,
            savedAnswer = answersMap["akt1_k2_q1"]?.answer ?: "",
            aiFeedback = answersMap["akt1_k2_q1"]?.aiFeedback ?: "",
            isAiEnabled = config.isAiEnabled,
            onSaveAnswer = { onSaveAnswer("akt1_k2_q1", it) },
            onRequestAiFeedback = {
                onRequestAiFeedback("akt1_k2_q1", k2q1Text, "Konsep etnosains dan kearifan lokal masyarakat Desa Colo")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        val k2q2Text = "2. Mengapa penting mengintegrasikan pengetahuan lokal masyarakat dengan literasi konservasi?"
        AutosaveAnswerCard(
            questionId = "akt1_k2_q2",
            numberLabel = "Kegiatan 2 • Pertanyaan 2",
            questionText = k2q2Text,
            savedAnswer = answersMap["akt1_k2_q2"]?.answer ?: "",
            aiFeedback = answersMap["akt1_k2_q2"]?.aiFeedback ?: "",
            isAiEnabled = config.isAiEnabled,
            onSaveAnswer = { onSaveAnswer("akt1_k2_q2", it) },
            onRequestAiFeedback = {
                onRequestAiFeedback("akt1_k2_q2", k2q2Text, "Integrasi etnosains dengan pelestarian keanekaragaman hayati")
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // KEGIATAN 3 — PERUMUSAN HIPOTESIS (Bagian L)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KEGIATAN 3 — Perumusan Hipotesis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Tantangan Rumusan Masalah:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "“Bagaimana strategi budidaya yang tepat agar pemanfaatan ekonomi Parijoto tetap berjalan tanpa merusak kelestarian hutan lindung?”",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Format Hipotesis: “Jika masyarakat melakukan [tindakan], maka [dampak yang diharapkan].”",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tindakanInput,
                    onValueChange = {
                        tindakanInput = it
                        if (tindakanInput.isNotBlank() || dampakInput.isNotBlank()) {
                            customHypothesis = "Jika masyarakat melakukan ${tindakanInput.ifBlank { "[tindakan]" }}, maka ${dampakInput.ifBlank { "[dampak yang diharapkan]" }}."
                        }
                    },
                    label = { Text("Variabel Tindakan (Jika masyarakat melakukan...)") },
                    placeholder = { Text("contoh: budidaya mandiri ex-situ dengan paranet & stek batang") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dampakInput,
                    onValueChange = {
                        dampakInput = it
                        if (tindakanInput.isNotBlank() || dampakInput.isNotBlank()) {
                            customHypothesis = "Jika masyarakat melakukan ${tindakanInput.ifBlank { "[tindakan]" }}, maka ${dampakInput.ifBlank { "[dampak yang diharapkan]" }}."
                        }
                    },
                    label = { Text("Variabel Dampak (maka...)") },
                    placeholder = { Text("contoh: produksi buah meningkat dan populasi hutan lindung tetap lestari") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customHypothesis,
                    onValueChange = { customHypothesis = it },
                    label = { Text("Rumusan Hipotesis Lengkap Anda") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_hipotesis_akt1")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSaveAnswer("akt1_k3_hipotesis", customHypothesis) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_simpan_hipotesis")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Hipotesis")
                    }

                    Button(
                        onClick = {
                            onSaveAnswer("akt1_k3_hipotesis", customHypothesis)
                            onContinueToAktivitas2()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_lanjut_akt2")
                    ) {
                        Text("Lanjut Aktivitas 2")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showVideoModal) {
        DocumentaryVideoDialog(
            videoUrl = config.videoUrlAktivitas1,
            onDismiss = { showVideoModal = false }
        )
    }

    if (showQrModal) {
        QrScannerAndReferenceDialog(
            expectedQrCode = config.qrCodeAktivitas1,
            referenceUrl = config.refUrlAktivitas1,
            title = "Sumber Belajar Etnosains Parijoto",
            scientificSummary = "Parijoto (Medinilla speciosa) merupakan tumbuhan epifit/semak dari famili Melastomataceae di kawasan Gunung Muria. Studi etnobotani menunjukkan pemanfaatan buah Parijoto oleh masyarakat Desa Colo berhubungan erat dengan kandungan antioksidan polifenol, flavonoid, saponin, dan tanin.",
            onDismiss = { showQrModal = false }
        )
    }
}
