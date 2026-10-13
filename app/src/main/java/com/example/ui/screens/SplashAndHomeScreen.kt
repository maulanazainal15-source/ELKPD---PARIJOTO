package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ProgressEntity
import com.example.model.ResultEntity
import com.example.model.TeacherConfigEntity
import com.example.model.UserEntity
import com.example.viewmodel.ActivitySubTab
import com.example.viewmodel.MainNavTab
import java.util.Locale

@Composable
fun SplashScreen(
    config: TeacherConfigEntity,
    onStartClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F291E),
                        Color(0xFF1B4332),
                        Color(0xFF3C096C)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_parijoto_1791360700216),
                    contentDescription = "Ilustrasi Parijoto Gunung Muria",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = Color(0xFFF3D9FA),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "E-LKPD INTERAKTIF BIOLOGI • SMA/MA KELAS X / FASE E",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF3C096C),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "KEANEKARAGAMAN HAYATI",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Eksplorasi Etnosains Parijoto (Medinilla speciosa)",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFD8F3DC),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pendekatan Problem Based Learning (PBL) • Alokasi 2 JP (2 x 45 Menit)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Penulis: ${config.authorName} • Tahun ${config.moduleYear}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFF3D9FA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onStartClicked,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF52B788),
                    contentColor = Color(0xFF081C15)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("splash_start_button")
            ) {
                Text(
                    text = "Masuk ke E-LKPD Digital",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun BerandaScreen(
    activeUser: UserEntity?,
    config: TeacherConfigEntity,
    progressList: List<ProgressEntity>,
    latestResult: ResultEntity?,
    onSaveIdentity: (String, String, String, String, String) -> Unit,
    onNavigateMainTab: (MainNavTab) -> Unit,
    onNavigateSubTab: (ActivitySubTab) -> Unit,
    onOpenTeacherDashboard: () -> Unit,
    onSyncOfflineData: () -> Unit
) {
    var showIdentityDialog by remember { mutableStateOf(false) }

    val progressMap = progressList.associateBy { it.activityId }
    val pendahuluanPct = progressMap["PENDAHULUAN"]?.progress ?: 0
    val cpTpPct = progressMap["CP_TP"]?.progress ?: 0
    val akt1Pct = progressMap["AKTIVITAS_1"]?.progress ?: 0
    val akt2Pct = progressMap["AKTIVITAS_2"]?.progress ?: 0
    val evalPct = progressMap["EVALUASI"]?.progress ?: 0
    val overallPct = (pendahuluanPct + cpTpPct + akt1Pct + akt2Pct + evalPct) / 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // HERO BANNER CARD
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.height(185.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_parijoto_1791360700216),
                    contentDescription = "Banner Etnosains Parijoto Gunung Muria",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color(0xFF081C15).copy(alpha = 0.88f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "IPA BIOLOGI • KELAS ${config.classPhase}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = config.moduleTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Penulis: ${config.authorName} (${config.moduleYear}) • ${config.schoolName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD8F3DC)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // KARTU IDENTITAS PESERTA DIDIK & MODE BELAJAR
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (activeUser == null || activeUser.nama.isBlank()) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
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
                            imageVector = if (activeUser?.modeKerja == "INDIVIDU") Icons.Default.Person else Icons.Default.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (activeUser == null || activeUser.nama.isBlank()) {
                                    "Identitas Peserta Didik Belum Diisi"
                                } else {
                                    activeUser.nama
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (activeUser == null || activeUser.nama.isBlank()) {
                                    "Silakan lengkapi Nama, NIS, Kelas, dan Mode Kelompok sebelum mulai."
                                } else {
                                    "NIS: ${activeUser.nis} • Kelas: ${activeUser.kelas} • ${activeUser.kelompok} (${activeUser.modeKerja})"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = { showIdentityDialog = true },
                        modifier = Modifier.testTag("btn_isi_identitas")
                    ) {
                        Text(if (activeUser == null || activeUser.nama.isBlank()) "Isi Data" else "Ubah")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // KARTU PROGRESS PEMBELAJARAN & SINKRONISASI OFFLINE
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Progress Pembelajaran E-LKPD",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mode Offline Aktif (Tersimpan di Room Database)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "$overallPct% Selesai",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { overallPct / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rincian status 5 bagian sesuai spesifikasi AE
                ProgressRowItem("Pendahuluan & Petunjuk", pendahuluanPct)
                ProgressRowItem("Capaian & Tujuan Pembelajaran (CP & TP)", cpTpPct)
                ProgressRowItem("Aktivitas 1: Dinamika Etnosains & Masalah", akt1Pct)
                ProgressRowItem("Aktivitas 2: Investigasi Saintifik", akt2Pct)
                ProgressRowItem("Uji Kemampuan (PG HOTS & Uraian)", evalPct)

                if (latestResult != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nilai Evaluasi Terakhir:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.1f", latestResult.finalScore)} (${latestResult.gradingMode})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onSyncOfflineData,
                        modifier = Modifier.testTag("btn_sync_offline")
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sinkronkan Data Lokal")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MENU DASHBOARD 8 KARTU INTERAKTIF (Sesuai Spesifikasi F. BERANDA)
        Text(
            text = "Menu Utama Pembelajaran",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Tombol Utama: [Mulai Pembelajaran]
        Button(
            onClick = {
                if (activeUser == null || activeUser.nama.isBlank()) {
                    showIdentityDialog = true
                } else {
                    onNavigateSubTab(ActivitySubTab.PENDAHULUAN)
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("menu_mulai_pembelajaran")
        ) {
            Icon(Icons.Default.PlayCircleFilled, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Mulai Pembelajaran (Alur PBL Lengkap)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid 2 Kolom untuk 8 menu Beranda
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardMenuCard(
                title = "Petunjuk",
                subtitle = "Panduan 8 Langkah E-LKPD",
                statusText = if (pendahuluanPct == 100) "Selesai ✓" else "$pendahuluanPct%",
                icon = Icons.Default.HelpOutline,
                accentColor = MaterialTheme.colorScheme.primary,
                testTag = "menu_petunjuk",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSubTab(ActivitySubTab.PENDAHULUAN) }
            )
            DashboardMenuCard(
                title = "CP & TP",
                subtitle = "Capaian & 6 Tujuan",
                statusText = if (cpTpPct == 100) "Selesai ✓" else "$cpTpPct%",
                icon = Icons.Default.Flag,
                accentColor = MaterialTheme.colorScheme.secondary,
                testTag = "menu_cp_tp",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSubTab(ActivitySubTab.CP_TP) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardMenuCard(
                title = "Aktivitas 1",
                subtitle = "Dinamika Etnosains & Masalah",
                statusText = "$akt1Pct%",
                icon = Icons.Default.EmojiNature,
                accentColor = MaterialTheme.colorScheme.primary,
                testTag = "menu_aktivitas_1",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSubTab(ActivitySubTab.AKTIVITAS_1) }
            )
            DashboardMenuCard(
                title = "Aktivitas 2",
                subtitle = "Investigasi Saintifik",
                statusText = "$akt2Pct%",
                icon = Icons.Default.Biotech,
                accentColor = MaterialTheme.colorScheme.secondary,
                testTag = "menu_aktivitas_2",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateSubTab(ActivitySubTab.AKTIVITAS_2) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardMenuCard(
                title = "Uji Kemampuan",
                subtitle = "5 PG HOTS & 4 Uraian",
                statusText = "$evalPct%",
                icon = Icons.Default.Quiz,
                accentColor = MaterialTheme.colorScheme.tertiary,
                testTag = "menu_uji_kemampuan",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateMainTab(MainNavTab.EVALUASI) }
            )
            DashboardMenuCard(
                title = "Hasil Saya",
                subtitle = "Nilai, Rubrik & Export PDF",
                statusText = if (latestResult != null) "Skor: ${latestResult.finalScore.toInt()}" else "Belum ada",
                icon = Icons.Default.Assessment,
                accentColor = MaterialTheme.colorScheme.primary,
                testTag = "menu_hasil_saya",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateMainTab(MainNavTab.HASIL) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardMenuCard(
                title = "Profil",
                subtitle = "Data Siswa & Kelompok",
                statusText = activeUser?.modeKerja ?: "Atur Profil",
                icon = Icons.Default.AccountCircle,
                accentColor = MaterialTheme.colorScheme.secondary,
                testTag = "menu_profil",
                modifier = Modifier.weight(1f),
                onClick = { onNavigateMainTab(MainNavTab.PROFIL) }
            )
            DashboardMenuCard(
                title = "Portal Guru",
                subtitle = "Rubrik, Nilai & Konfigurasi",
                statusText = "Mode Admin/Guru",
                icon = Icons.Default.AdminPanelSettings,
                accentColor = MaterialTheme.colorScheme.tertiary,
                testTag = "menu_portal_guru",
                modifier = Modifier.weight(1f),
                onClick = onOpenTeacherDashboard
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showIdentityDialog) {
        StudentIdentityModal(
            currentUser = activeUser,
            onDismiss = { showIdentityDialog = false },
            onSave = { nama, nis, kelas, kelompok, mode ->
                onSaveIdentity(nama, nis, kelas, kelompok, mode)
                showIdentityDialog = false
            }
        )
    }
}

@Composable
private fun ProgressRowItem(label: String, pct: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (pct == 100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.35f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = if (pct == 100) "✓ 100%" else "$pct%",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DashboardMenuCard(
    title: String,
    subtitle: String,
    statusText: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.14f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Surface(
                    color = accentColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Composable
fun StudentIdentityModal(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var nama by remember { mutableStateOf(currentUser?.nama ?: "") }
    var nis by remember { mutableStateOf(currentUser?.nis ?: "") }
    var kelas by remember { mutableStateOf(currentUser?.kelas ?: "X E-1") }
    var kelompok by remember { mutableStateOf(currentUser?.kelompok ?: "Kelompok 1 (Muria Lestari)") }
    var modeKerja by remember { mutableStateOf(currentUser?.modeKerja ?: "KELOMPOK") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (nama.trim().length < 2) {
                        errorMsg = "Nama lengkap wajib diisi minimal 2 karakter."
                        return@Button
                    }
                    if (nis.trim().length < 3) {
                        errorMsg = "Nomor Induk Siswa (NIS) wajib diisi dengan benar."
                        return@Button
                    }
                    if (kelas.isBlank()) {
                        errorMsg = "Kelas wajib diisi."
                        return@Button
                    }
                    onSave(nama, nis, kelas, kelompok, modeKerja)
                },
                modifier = Modifier.testTag("btn_simpan_identitas")
            ) {
                Text("Simpan Identitas")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        title = {
            Text(
                text = "Identitas Peserta Didik",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Pilih Mode Pengerjaan E-LKPD:",
                    style = MaterialTheme.typography.labelLarge
                )
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
                    onValueChange = {
                        nama = it
                        errorMsg = null
                    },
                    label = { Text("Nama Lengkap Peserta Didik *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_nama_siswa")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nis,
                    onValueChange = {
                        nis = it
                        errorMsg = null
                    },
                    label = { Text("Nomor Induk Siswa (NIS) *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_nis_siswa")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = kelas,
                    onValueChange = {
                        kelas = it
                        errorMsg = null
                    },
                    label = { Text("Kelas / Fase (Contoh: X E-1) *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_kelas_siswa")
                )

                AnimatedVisibility(visible = modeKerja == "KELOMPOK") {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = kelompok,
                            onValueChange = { kelompok = it },
                            label = { Text("Nama / Nomor Kelompok Diskusi") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_kelompok_siswa")
                        )
                    }
                }

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}
