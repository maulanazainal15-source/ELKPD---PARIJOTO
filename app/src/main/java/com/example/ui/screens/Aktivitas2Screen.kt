package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.domain.ElpkdCurriculumData
import com.example.model.AnswerEntity
import com.example.model.ImageEntity
import com.example.model.TeacherConfigEntity
import com.example.ui.components.AutosaveAnswerCard
import com.example.ui.components.PblPhaseIndicatorBanner
import com.example.ui.components.QrScannerAndReferenceDialog
import java.io.File
import java.io.FileOutputStream

@Composable
fun Aktivitas2Section(
    config: TeacherConfigEntity,
    answersMap: Map<String, AnswerEntity>,
    savedImages: List<ImageEntity>,
    onSaveAnswer: (String, String) -> Unit,
    onAddImage: (String, String, String) -> Unit,
    onDeleteImage: (Int) -> Unit,
    onRequestAiFeedback: (String, String, String) -> Unit,
    onContinueToEvaluation: () -> Unit
) {
    val context = LocalContext.current
    var showIgQrModal by remember { mutableStateOf(false) }

    // State untuk upload gambar morfologi Parijoto (Bagian N)
    var pendingImagePath by remember { mutableStateOf<String?>(null) }
    var captionInput by remember {
        mutableStateOf("Pengamatan morfologi daun melengkung sejajar (curvinervis) dan klaster buah buni merah-ungu Medinilla speciosa.")
    }
    var bagianInput by remember { mutableStateOf("Daun, Batang & Buah Buni Parijoto") }
    var imageIdToDelete by remember { mutableStateOf<Int?>(null) }
    var showClearPreviewConfirm by remember { mutableStateOf(false) }

    // Launcher Kamera (TakePicturePreview -> simpan ke file lokal internal)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val savedPath = saveBitmapToInternalStorage(context, bitmap)
            if (savedPath != null) {
                pendingImagePath = savedPath
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

    // Launcher Galeri (Zero-Permission Android Photo Picker sesuai Google Play Policy)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val copiedPath = copyUriToInternalStorage(context, uri)
            pendingImagePath = copiedPath ?: uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Indikator Fase 3 PBL: Membimbing Penyelidikan Individu/Kelompok
        PblPhaseIndicatorBanner(activePhaseNumber = 3)

        Spacer(modifier = Modifier.height(14.dp))

        // BAGIAN M: AKTIVITAS 2 — INVESTIGASI SAINTIFIK
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "AKTIVITAS 2 • INVESTIGASI SAINTIFIK",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = ElpkdCurriculumData.MATERI_AKTIVITAS_2_RINGKASAN,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                ElpkdCurriculumData.kartuDataSaintifikAktivitas2.forEach { (judul, sub, isi) ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Biotech,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = judul,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = sub,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = isi,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BAGIAN N: VISUALISASI MANDIRI (Cari dan unggah gambar morfologi Parijoto)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "VISUALISASI MANDIRI — Morfologi Parijoto",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cari dan unggah gambar morfologi Parijoto (daun, bunga, atau buah buni) melalui Kamera atau Galeri, lalu tuliskan deskripsi morfologinya.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pilihan [Ambil Foto] & [Pilih dari Galeri] + Spesimen Referensi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_ambil_foto")
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ambil Foto")
                    }

                    FilledTonalButton(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_pilih_galeri")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pilih dari Galeri")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = {
                        pendingImagePath = "SPECIMEN_BOTANI_MURIA"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_gunakan_spesimen_referensi")
                ) {
                    Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gunakan Foto Spesimen Herbarium Muria (Mode Offline)")
                }

                // Preview Gambar yang sedang dipilih sebelum disimpan
                if (pendingImagePath != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Preview Gambar Morfologi:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (pendingImagePath == "SPECIMEN_BOTANI_MURIA") {
                                Image(
                                    painter = painterResource(id = R.drawable.img_hero_parijoto_1791360700216),
                                    contentDescription = "Preview Spesimen Parijoto",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            } else {
                                AsyncImage(
                                    model = pendingImagePath,
                                    contentDescription = "Preview Foto Morfologi",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = bagianInput,
                                onValueChange = { bagianInput = it },
                                label = { Text("Organ yang Diamati (Daun / Bunga / Buah / Batang)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = captionInput,
                                onValueChange = { captionInput = it },
                                label = { Text("Caption / Deskripsi Morfologi Parijoto") },
                                minLines = 2,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_caption_gambar")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Tombol Ganti, Hapus Preview, dan Simpan Gambar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        galleryLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ganti")
                                }
                                OutlinedButton(
                                    onClick = { showClearPreviewConfirm = true },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Hapus")
                                }
                                Button(
                                    onClick = {
                                        onAddImage(pendingImagePath!!, captionInput, bagianInput)
                                        pendingImagePath = null
                                    },
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .testTag("btn_simpan_gambar")
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Simpan")
                                }
                            }
                        }
                    }
                }

                // Daftar Gambar Morfologi yang Sudah Tersimpan
                if (savedImages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Galeri Dokumentasi Morfologi Tersimpan (${savedImages.size}):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    savedImages.forEach { img ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (img.filePath == "SPECIMEN_BOTANI_MURIA") {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_hero_parijoto_1791360700216),
                                        contentDescription = img.caption,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                } else {
                                    AsyncImage(
                                        model = img.filePath,
                                        contentDescription = img.caption,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = img.bagianTanaman,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = img.caption,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Row {
                                        IconButton(
                                            onClick = {
                                                pendingImagePath = img.filePath
                                                captionInput = img.caption
                                                bagianInput = img.bagianTanaman
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Ganti/Edit Gambar",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(
                                            onClick = { imageIdToDelete = img.id },
                                            modifier = Modifier.testTag("btn_hapus_gambar_${img.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Hapus Gambar Tersimpan",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BAGIAN O: ANALISIS BIOLOGI (Tabel Interaktif Horizontal Scroll)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ANALISIS BIOLOGI — Tabel Matriks Etnosains",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Geser tabel ke kanan/kiri (Horizontal Scroll) untuk mengisi setiap dimensi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Scrollable Table
                val horizontalScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScroll)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    // Header Row (6 Kolom sesuai Bagian O)
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                            .padding(vertical = 8.dp, horizontal = 6.dp)
                    ) {
                        TableHeaderCell("DIMENSI PENGETAHUAN", 170.dp)
                        TableHeaderCell("PENGETAHUAN LOKAL", 210.dp)
                        TableHeaderCell("VALIDASI ILMIAH", 210.dp)
                        TableHeaderCell("MANFAAT UTAMA", 200.dp)
                        TableHeaderCell("SIFAT BUDIDAYA", 200.dp)
                        TableHeaderCell("INDIKATOR ALAM", 200.dp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ElpkdCurriculumData.tabelDimensiBiologi.forEach { row ->
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(170.dp)
                                    .padding(end = 8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = row.dimensionName,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            InteractiveTableCell(
                                key = "tbl_${row.id}_lokal",
                                initialValue = answersMap["tbl_${row.id}_lokal"]?.answer ?: "",
                                hint = row.hintLokal,
                                onSave = onSaveAnswer
                            )
                            InteractiveTableCell(
                                key = "tbl_${row.id}_ilmiah",
                                initialValue = answersMap["tbl_${row.id}_ilmiah"]?.answer ?: "",
                                hint = row.hintIlmiah,
                                onSave = onSaveAnswer
                            )
                            InteractiveTableCell(
                                key = "tbl_${row.id}_manfaat",
                                initialValue = answersMap["tbl_${row.id}_manfaat"]?.answer ?: "",
                                hint = row.hintManfaat,
                                onSave = onSaveAnswer
                            )
                            InteractiveTableCell(
                                key = "tbl_${row.id}_budidaya",
                                initialValue = answersMap["tbl_${row.id}_budidaya"]?.answer ?: "",
                                hint = row.hintBudidaya,
                                onSave = onSaveAnswer
                            )
                            InteractiveTableCell(
                                key = "tbl_${row.id}_indikator",
                                initialValue = answersMap["tbl_${row.id}_indikator"]?.answer ?: "",
                                hint = row.hintIndikator,
                                onSave = onSaveAnswer
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Indikator Fase 4 PBL: Mengembangkan dan Menyajikan Hasil
        PblPhaseIndicatorBanner(activePhaseNumber = 4)

        Spacer(modifier = Modifier.height(14.dp))

        // BAGIAN P: REKONSTRUKSI ETNOSAINS
        val pRekonstruksi = "Bandingkan pengetahuan lokal masyarakat dengan validasi ilmiah yang Anda temukan untuk mendapatkan titik temu kognitif."
        AutosaveAnswerCard(
            questionId = "akt2_rekonstruksi",
            numberLabel = "Rekonstruksi Etnosains",
            questionText = pRekonstruksi,
            savedAnswer = answersMap["akt2_rekonstruksi"]?.answer ?: "",
            aiFeedback = answersMap["akt2_rekonstruksi"]?.aiFeedback ?: "",
            isAiEnabled = config.isAiEnabled,
            onSaveAnswer = { onSaveAnswer("akt2_rekonstruksi", it) },
            onRequestAiFeedback = {
                onRequestAiFeedback("akt2_rekonstruksi", pRekonstruksi, "Titik temu kognitif pengetahuan lokal dan validasi biokimia")
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // BAGIAN Q: STUDI LITERATUR & STATUS KONSERVASI (Indikasi Geografis)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "STUDI LITERATUR & STATUS KONSERVASI",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Akses sumber literatur resmi mengenai perlindungan hukum dan konservasi Indikasi Geografis (IG) Parijoto Gunung Muria melalui tombol di bawah ini:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showIgQrModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_scan_qr_ig")
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SCAN QR")
                    }
                    FilledTonalButton(
                        onClick = { showIgQrModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_buka_referensi_ig")
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BUKA REFERENSI")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val qIgText = "Mengapa pemberian status “Indikasi Geografis (IG)” penting bagi kelestarian Parijoto?"
        AutosaveAnswerCard(
            questionId = "akt2_studi_ig",
            numberLabel = "Studi Literatur • Indikasi Geografis (IG)",
            questionText = qIgText,
            savedAnswer = answersMap["akt2_studi_ig"]?.answer ?: "",
            aiFeedback = answersMap["akt2_studi_ig"]?.aiFeedback ?: "",
            isAiEnabled = config.isAiEnabled,
            onSaveAnswer = { onSaveAnswer("akt2_studi_ig", it) },
            onRequestAiFeedback = {
                onRequestAiFeedback("akt2_studi_ig", qIgText, "Pentingnya status Indikasi Geografis (IG) bagi konservasi Parijoto")
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // BAGIAN R: ANALISIS SENYAWA (Dengan peringatan wajib sumber ilmiah)
        val qSenyawaText = "Jelaskan manfaat antioksidan yang terkandung dalam Parijoto bagi kesehatan tubuh manusia."
        AutosaveAnswerCard(
            questionId = "akt2_senyawa",
            numberLabel = "Analisis Senyawa Metabolit Sekunder",
            questionText = qSenyawaText,
            savedAnswer = answersMap["akt2_senyawa"]?.answer ?: "",
            aiFeedback = answersMap["akt2_senyawa"]?.aiFeedback ?: "",
            warningNote = "Jawaban harus berdasarkan sumber ilmiah, bukan sekadar klaim kesehatan.",
            isAiEnabled = config.isAiEnabled,
            onSaveAnswer = { onSaveAnswer("akt2_senyawa", it) },
            onRequestAiFeedback = {
                onRequestAiFeedback("akt2_senyawa", qSenyawaText, "Mekanisme ilmiah antioksidan flavonoid, antosianin, saponin, tanin, dan β-sitosterol")
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // BAGIAN S: ANALISIS KRITIS & SOLUSI (Dua editor jawaban terpisah)
        Text(
            text = "ANALISIS KRITIS & SOLUSI PELESTARIAN",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val qKritis1 = "1. Bagaimana hasil validasi ilmiah dapat meyakinkan masyarakat beralih dari mengambil bibit liar ke budidaya mandiri?"
        AutosaveAnswerCard(
            questionId = "akt2_kritis_1",
            numberLabel = "Analisis Kritis • Pertanyaan 1",
            questionText = qKritis1,
            savedAnswer = answersMap["akt2_kritis_1"]?.answer ?: "",
            aiFeedback = answersMap["akt2_kritis_1"]?.aiFeedback ?: "",
            isAiEnabled = config.isAiEnabled,
            onSaveAnswer = { onSaveAnswer("akt2_kritis_1", it) },
            onRequestAiFeedback = {
                onRequestAiFeedback("akt2_kritis_1", qKritis1, "Transisi dari panen liar ke budidaya mandiri berbasis bukti ilmiah")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        val qKritis2 = "2. Mengapa menjaga kelestarian kearifan lokal Muria juga berarti menjaga keanekaragaman hayati secara global?"
        AutosaveAnswerCard(
            questionId = "akt2_kritis_2",
            numberLabel = "Analisis Kritis • Pertanyaan 2",
            questionText = qKritis2,
            savedAnswer = answersMap["akt2_kritis_2"]?.answer ?: "",
            aiFeedback = answersMap["akt2_kritis_2"]?.aiFeedback ?: "",
            isAiEnabled = config.isAiEnabled,
            onSaveAnswer = { onSaveAnswer("akt2_kritis_2", it) },
            onRequestAiFeedback = {
                onRequestAiFeedback("akt2_kritis_2", qKritis2, "Hubungan kearifan lokal Gunung Muria dengan biodiversitas global")
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Indikator Fase 5 PBL: Menganalisis dan Mengevaluasi Proses Pemecahan Masalah
        PblPhaseIndicatorBanner(activePhaseNumber = 5)

        Spacer(modifier = Modifier.height(14.dp))

        // BAGIAN T: KESIMPULAN (Editor Kesimpulan + Tombol Simpan + Tombol Lanjut)
        val qKesimpulan = "Bagaimana integrasi antara Etnosains (Budaya) dan Biologi dapat memperkuat kesadaran kita untuk melestarikan lingkungan?"
        var kesimpulanLocal by remember(answersMap["akt2_kesimpulan"]?.answer) {
            mutableStateOf(answersMap["akt2_kesimpulan"]?.answer ?: "")
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "KESIMPULAN AKHIR PENYELIDIKAN",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = qKesimpulan,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = kesimpulanLocal,
                    onValueChange = {
                        kesimpulanLocal = it
                        onSaveAnswer("akt2_kesimpulan", it)
                    },
                    placeholder = { Text("Rumuskan kesimpulan sintesis Etnosains dan Biologi...") },
                    minLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_kesimpulan_akt2")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSaveAnswer("akt2_kesimpulan", kesimpulanLocal) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_simpan_kesimpulan")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan")
                    }

                    Button(
                        onClick = {
                            onSaveAnswer("akt2_kesimpulan", kesimpulanLocal)
                            onContinueToEvaluation()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_lanjut_evaluasi")
                    ) {
                        Text("Lanjut Evaluasi")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Dialog Konfirmasi Hapus Gambar Tersimpan (Sesuai Bagian N & AF: Jangan menghapus tanpa konfirmasi)
    if (imageIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { imageIdToDelete = null },
            title = { Text("Konfirmasi Hapus Gambar") },
            text = {
                Text("Apakah Anda yakin ingin menghapus gambar morfologi Parijoto yang sudah tersimpan ini?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteImage(imageIdToDelete!!)
                        imageIdToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Hapus Gambar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { imageIdToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showClearPreviewConfirm) {
        AlertDialog(
            onDismissRequest = { showClearPreviewConfirm = false },
            title = { Text("Batalkan Pilihan Gambar?") },
            text = { Text("Apakah Anda yakin ingin menghapus preview gambar yang sedang dipilih?") },
            confirmButton = {
                Button(
                    onClick = {
                        pendingImagePath = null
                        showClearPreviewConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearPreviewConfirm = false }) {
                    Text("Kembali")
                }
            }
        )
    }

    if (showIgQrModal) {
        QrScannerAndReferenceDialog(
            expectedQrCode = config.qrCodeAktivitas2,
            referenceUrl = config.refUrlIndikasiGeografis,
            title = "Studi Literatur & Status Konservasi IG",
            scientificSummary = "Sertifikasi Indikasi Geografis (IG) melindungi keaslian varietas lokal Parijoto Gunung Muria sekaligus menetapkan buku persyaratan budidaya berkelanjutan. Dengan standar IG, petani wajib melakukan perbanyakan bibit secara mandiri (ex-situ) dan dilarang merusak tegakan alami di zona hutan lindung.",
            onDismiss = { showIgQrModal = false }
        )
    }
}

@Composable
private fun TableHeaderCell(title: String, width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun InteractiveTableCell(
    key: String,
    initialValue: String,
    hint: String,
    onSave: (String, String) -> Unit
) {
    var text by remember(key, initialValue) { mutableStateOf(initialValue) }
    Box(
        modifier = Modifier
            .width(205.dp)
            .padding(horizontal = 4.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onSave(key, it)
            },
            placeholder = { Text(hint, style = MaterialTheme.typography.bodySmall) },
            minLines = 3,
            maxLines = 4,
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cell_$key")
        )
    }
}

private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
    return try {
        val file = File(context.filesDir, "morfologi_parijoto_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}

private fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "galeri_parijoto_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            inputStream.copyTo(out)
        }
        inputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}
