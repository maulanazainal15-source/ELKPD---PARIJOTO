package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AnswerEntity
import com.example.model.ProgressEntity
import com.example.model.ResultEntity
import com.example.model.TeacherConfigEntity
import com.example.model.UserEntity
import com.example.utils.PdfExportGenerator
import java.util.Locale

/**
 * LAYAR PROFIL PESERTA DIDIK & AKSES PORTAL GURU (Bagian G, B.22, AH)
 */
@Composable
fun ProfileScreen(
    activeUser: UserEntity?,
    allUsers: List<UserEntity>,
    onSaveIdentity: (String, String, String, String, String) -> Unit,
    onSwitchUser: (UserEntity) -> Unit,
    onCreateNewProfile: () -> Unit,
    onOpenTeacherPortal: () -> Unit
) {
    var nama by remember(activeUser) { mutableStateOf(activeUser?.nama ?: "") }
    var nis by remember(activeUser) { mutableStateOf(activeUser?.nis ?: "") }
    var kelas by remember(activeUser) { mutableStateOf(activeUser?.kelas ?: "X E-1") }
    var kelompok by remember(activeUser) { mutableStateOf(activeUser?.kelompok ?: "Kelompok 1") }
    var modeKerja by remember(activeUser) { mutableStateOf(activeUser?.modeKerja ?: "KELOMPOK") }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Profil & Identitas Peserta Didik",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Data tersimpan secara lokal pada perangkat untuk mode offline & laporan PDF.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Mode Pembelajaran:", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = modeKerja == "INDIVIDU",
                        onClick = { modeKerja = "INDIVIDU" },
                        label = { Text("INDIVIDU") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    FilterChip(
                        selected = modeKerja == "KELOMPOK",
                        onClick = { modeKerja = "KELOMPOK" },
                        label = { Text("KELOMPOK") },
                        leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Lengkap Peserta Didik") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profil_input_nama")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nis,
                    onValueChange = { nis = it },
                    label = { Text("Nomor Induk Siswa (NIS)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profil_input_nis")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = kelas,
                    onValueChange = { kelas = it },
                    label = { Text("Kelas / Fase (Contoh: X E-1)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profil_input_kelas")
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (modeKerja == "KELOMPOK") {
                    OutlinedTextField(
                        value = kelompok,
                        onValueChange = { kelompok = it },
                        label = { Text("Nama Kelompok Diskusi") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profil_input_kelompok")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (validationMessage != null) {
                    Text(
                        text = validationMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Button(
                    onClick = {
                        if (nama.trim().length < 2 || nis.trim().length < 3) {
                            validationMessage = "Mohon isi Nama Lengkap (min. 2 huruf) dan NIS (min. 3 digit)."
                            return@Button
                        }
                        validationMessage = null
                        onSaveIdentity(nama, nis, kelas, kelompok, modeKerja)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_simpan_profil")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Perubahan Profil")
                }
            }
        }

        // Daftar Akun Peserta Didik Lokal (Jika tablet/HP dipakai bergantian di kelas)
        if (allUsers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Peserta Didik pada Perangkat Ini",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(onClick = onCreateNewProfile) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Siswa Baru")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    allUsers.forEach { u ->
                        Surface(
                            color = if (u.id == activeUser?.id) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = u.nama.ifBlank { "(Nama Belum Diisi)" },
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Text(
                                        text = "NIS: ${u.nis} • ${u.kelas} • ${u.kelompok}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                if (u.id != activeUser?.id) {
                                    OutlinedButton(onClick = { onSwitchUser(u) }) {
                                        Text("Aktifkan")
                                    }
                                } else {
                                    Text(
                                        text = "Aktif ✓",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kartu Akses Role ADMIN / GURU (Bagian AH)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mode Role: ADMIN / GURU BIOLOGI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Guru dapat melihat daftar peserta didik, memantau progress, menilai jawaban uraian menggunakan rubrik 6 aspek, mengunduh hasil PDF, serta mengonfigurasi URL Video, QR, Referensi, dan Bobot Penilaian.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenTeacherPortal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_buka_dashboard_guru")
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buka Dashboard Guru (PIN Default: 2026)")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * DASHBOARD GURU / ADMIN (Bagian AH & AJ)
 * Mencakup:
 * 1. Rekap Peserta Didik, Progress, Jawaban, Hasil Evaluasi & Penilaian Rubrik Uraian Manual (Skala 1-4)
 * 2. Konfigurasi Konten E-LKPD Dinamis (Sekolah, Judul, Bobot PG/Uraian, Bobot 6 Aspek Rubrik, URL Video, URL Referensi, Kode QR, Mode AI)
 */
@Composable
fun TeacherDashboardDialog(
    config: TeacherConfigEntity,
    allUsers: List<UserEntity>,
    allProgress: List<ProgressEntity>,
    allAnswers: List<AnswerEntity>,
    allResults: List<ResultEntity>,
    onGradeStudentResult: (ResultEntity, List<Int>, String) -> Unit,
    onUpdateConfig: (TeacherConfigEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isPinVerified by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("2026") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedResultForGrading by remember { mutableStateOf<ResultEntity?>(null) }

    // State Konfigurasi Guru (Bagian AJ)
    var schoolName by remember(config) { mutableStateOf(config.schoolName) }
    var moduleTitle by remember(config) { mutableStateOf(config.moduleTitle) }
    var videoUrl by remember(config) { mutableStateOf(config.videoUrlAktivitas1) }
    var refUrl1 by remember(config) { mutableStateOf(config.refUrlAktivitas1) }
    var qrCode1 by remember(config) { mutableStateOf(config.qrCodeAktivitas1) }
    var refUrlIg by remember(config) { mutableStateOf(config.refUrlIndikasiGeografis) }
    var qrCode2 by remember(config) { mutableStateOf(config.qrCodeAktivitas2) }
    var mcqWeight by remember(config) { mutableStateOf(config.mcqWeightPercent.toString()) }
    var essayWeight by remember(config) { mutableStateOf(config.essayWeightPercent.toString()) }
    var isAiEnabled by remember(config) { mutableStateOf(config.isAiEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            OutlinedButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tutup Portal Guru")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dashboard Admin / Guru E-LKPD", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            if (!isPinVerified) {
                Column {
                    Text(
                        text = "Masukkan PIN Akses Guru untuk membuka panel penilaian rubrik dan pengaturan konten E-LKPD (Default PIN: ${config.teacherPin}).",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it
                            pinError = null
                        },
                        label = { Text("PIN Guru") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pin_guru")
                    )
                    if (pinError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pinError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (pinInput.trim() == config.teacherPin) {
                                isPinVerified = true
                            } else {
                                pinError = "PIN tidak sesuai. Gunakan PIN: ${config.teacherPin}"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_verifikasi_pin_guru")
                    ) {
                        Text("Masuk Dashboard Guru")
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Siswa & Penilaian") },
                            icon = { Icon(Icons.Default.Grade, contentDescription = null) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Pengaturan Konten") },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedTab == 0) {
                        Text(
                            text = "Daftar Peserta Didik Terdaftar: ${allUsers.size} Siswa",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (allResults.isEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Belum ada peserta didik yang men-submit Evaluasi Akhir. Jawaban aktivitas yang sedang dikerjakan tetap tersimpan otomatis.",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            allResults.forEach { res ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "${res.namaSiswa} (NIS: ${res.nisSiswa} • ${res.kelasSiswa})",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "PG: ${res.mcqScore.toInt()} | Uraian: ${res.essayScore?.let { String.format(Locale.US, "%.1f", it) } ?: "Belum dinilai"} | Akhir: ${String.format(Locale.US, "%.1f", res.finalScore)}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Status: ${res.gradingMode}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { selectedResultForGrading = res },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Beri Nilai Rubrik")
                                            }
                                            OutlinedButton(
                                                onClick = {
                                                    val u = allUsers.find { it.id == res.userId } ?: UserEntity(
                                                        id = res.userId,
                                                        nama = res.namaSiswa,
                                                        nis = res.nisSiswa,
                                                        kelas = res.kelasSiswa,
                                                        kelompok = res.kelompokSiswa
                                                    )
                                                    val uProg = allProgress.filter { it.userId == res.userId }
                                                    val uAns = allAnswers.filter { it.userId == res.userId }.associateBy { it.questionId }
                                                    val pdf = PdfExportGenerator.generateAndShareStudentReportPdf(
                                                        context, u, config, uProg, uAns, res
                                                    )
                                                    if (pdf != null) {
                                                        PdfExportGenerator.sharePdfFile(context, pdf)
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Default.PictureAsPdf, contentDescription = "Unduh PDF", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Form Penilaian Rubrik 6 Aspek (Skala 1-4) oleh Guru
                        if (selectedResultForGrading != null) {
                            val target = selectedResultForGrading!!
                            val rubricScores = remember(target) { mutableStateListOf(3, 3, 3, 3, 3, 3) }
                            var feedbackGuru by remember(target) { mutableStateOf(target.teacherFeedback) }
                            val aspectNames = listOf(
                                "1. Ketepatan Konsep Biologi (1-4)",
                                "2. Penggunaan Data Saintifik (1-4)",
                                "3. Kemampuan Analisis (1-4)",
                                "4. Berpikir Kritis (1-4)",
                                "5. Solusi Konservasi (1-4)",
                                "6. Kemampuan Komunikasi (1-4)"
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Penilaian Rubrik Uraian: ${target.namaSiswa}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    aspectNames.forEachIndexed { idx, label ->
                                        Text(
                                            text = "$label: Skor ${rubricScores[idx]}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Slider(
                                            value = rubricScores[idx].toFloat(),
                                            onValueChange = { rubricScores[idx] = it.toInt().coerceIn(1, 4) },
                                            valueRange = 1f..4f,
                                            steps = 2
                                        )
                                    }
                                    OutlinedTextField(
                                        value = feedbackGuru,
                                        onValueChange = { feedbackGuru = it },
                                        label = { Text("Feedback / Catatan Guru") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            onGradeStudentResult(target, rubricScores.toList(), feedbackGuru)
                                            selectedResultForGrading = null
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Simpan Nilai Akhir Guru")
                                    }
                                }
                            }
                        }
                    } else {
                        // TAB 2: PENGATURAN KONTEN, BOBOT, URL VIDEO/REFERENSI/QR, DAN FITUR AI (Bagian AJ)
                        OutlinedTextField(
                            value = schoolName,
                            onValueChange = { schoolName = it },
                            label = { Text("Nama Sekolah / Satuan Pendidikan") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = moduleTitle,
                            onValueChange = { moduleTitle = it },
                            label = { Text("Judul E-LKPD") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = mcqWeight,
                                onValueChange = { mcqWeight = it },
                                label = { Text("Bobot PG (%)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = essayWeight,
                                onValueChange = { essayWeight = it },
                                label = { Text("Bobot Uraian (%)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = videoUrl,
                            onValueChange = { videoUrl = it },
                            label = { Text("URL Video Pembelajaran Aktivitas 1") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = refUrl1,
                            onValueChange = { refUrl1 = it },
                            label = { Text("URL Referensi Etnosains Aktivitas 1") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = qrCode1,
                            onValueChange = { qrCode1 = it },
                            label = { Text("Kode QR Aktivitas 1") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = refUrlIg,
                            onValueChange = { refUrlIg = it },
                            label = { Text("URL Studi Literatur Indikasi Geografis (IG)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = qrCode2,
                            onValueChange = { qrCode2 = it },
                            label = { Text("Kode QR Aktivitas 2 (IG & Konservasi)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Aktifkan Modul AI Tutor & AI Feedback",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Switch(
                                checked = isAiEnabled,
                                onCheckedChange = { isAiEnabled = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val mw = mcqWeight.toIntOrNull()?.coerceIn(0, 100) ?: 50
                                val ew = essayWeight.toIntOrNull()?.coerceIn(0, 100) ?: 50
                                onUpdateConfig(
                                    config.copy(
                                        schoolName = schoolName,
                                        moduleTitle = moduleTitle,
                                        videoUrlAktivitas1 = videoUrl,
                                        refUrlAktivitas1 = refUrl1,
                                        qrCodeAktivitas1 = qrCode1,
                                        refUrlIndikasiGeografis = refUrlIg,
                                        qrCodeAktivitas2 = qrCode2,
                                        mcqWeightPercent = mw,
                                        essayWeightPercent = ew,
                                        isAiEnabled = isAiEnabled
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_simpan_konfigurasi_guru")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simpan Konfigurasi Guru")
                        }
                    }
                }
            }
        }
    )
}
