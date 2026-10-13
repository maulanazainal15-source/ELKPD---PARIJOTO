package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.domain.ElpkdCurriculumData
import com.example.model.AnswerEntity
import com.example.model.ProgressEntity
import com.example.model.ResultEntity
import com.example.model.TeacherConfigEntity
import com.example.model.UserEntity
import com.example.ui.components.AutosaveAnswerCard
import com.example.ui.components.PblPhaseIndicatorBanner
import com.example.utils.PdfExportGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * LAYAR UJI KEMAMPUAN: EVALUASI ETNOSAINS & BIOLOGI (Bagian U, V, W)
 * Bagian A: 5 Soal Pilihan Ganda HOTS (Satu soal per halaman, A..E, autosave, tidak tampil kunci sebelum submit)
 * Bagian B: 4 Soal Uraian Komprehensif + Mode Penilaian Guru / AI
 */
@Composable
fun EvaluationScreen(
    config: TeacherConfigEntity,
    answersMap: Map<String, AnswerEntity>,
    isAiLoading: Boolean,
    onSaveAnswer: (String, String) -> Unit,
    onRequestAiFeedback: (String, String, String) -> Unit,
    onSubmitEvaluation: (Boolean) -> Unit,
    onBackToHome: () -> Unit
) {
    var activeEvalPart by remember { mutableIntStateOf(0) } // 0 = Bagian A (PG HOTS), 1 = Bagian B (Uraian)
    var currentMcqIndex by remember { mutableIntStateOf(0) }
    var useAiGradingMode by remember { mutableStateOf(false) }
    var showValidationError by remember { mutableStateOf<String?>(null) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    val mcqs = ElpkdCurriculumData.defaultMcqQuestions
    val essays = ElpkdCurriculumData.defaultEssayQuestions

    val answeredMcqCount = mcqs.count { !answersMap[it.id]?.answer.isNullOrBlank() }
    val answeredEssayCount = essays.count { !answersMap[it.id]?.answer.isNullOrBlank() }

    // Konfirmasi sebelum keluar dari evaluasi (Sesuai Bagian AF)
    BackHandler {
        showExitConfirmDialog = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        PblPhaseIndicatorBanner(activePhaseNumber = 5)

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "EVALUASI ETNOSAINS & BIOLOGI",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Pilihan Ganda HOTS: $answeredMcqCount/${mcqs.size} Terjawab • Soal Uraian: $answeredEssayCount/${essays.size} Terjawab",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TabRow(
            selectedTabIndex = activeEvalPart,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = activeEvalPart == 0,
                onClick = { activeEvalPart = 0 },
                text = { Text("Bagian A: PG HOTS (${answeredMcqCount}/5)") },
                icon = { Icon(Icons.Default.Quiz, contentDescription = null) },
                modifier = Modifier.testTag("tab_eval_mcq")
            )
            Tab(
                selected = activeEvalPart == 1,
                onClick = { activeEvalPart = 1 },
                text = { Text("Bagian B: Uraian (${answeredEssayCount}/4)") },
                icon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                modifier = Modifier.testTag("tab_eval_essay")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeEvalPart == 0) {
            // BAGIAN A: SOAL PILIHAN GANDA HOTS (Tampilan 1 soal per halaman)
            val question = mcqs[currentMcqIndex]
            val selectedOption = answersMap[question.id]?.answer ?: ""

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Nomor Soal & Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "SOAL HOTS NOMOR ${question.number} DARI ${mcqs.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = if (selectedOption.isNotBlank()) "✓ Tersimpan ($selectedOption)" else "Belum dipilih",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedOption.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (currentMcqIndex + 1) / mcqs.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Indikator HOTS
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Indikator: ${question.indicator}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stimulus Soal
                    Text(
                        text = question.stimulus,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pilihan A, B, C, D, E
                    question.options.forEach { (optKey, optText) ->
                        val isChosen = selectedOption == optKey
                        val borderColor = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        val bgColor = if (isChosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .border(
                                    width = if (isChosen) 2.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onSaveAnswer(question.id, optKey)
                                    showValidationError = null
                                }
                                .padding(12.dp)
                                .testTag("mcq_${question.number}_option_$optKey"),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                            ) {
                                Text(
                                    text = optKey,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = optText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigasi Soal: [Sebelumnya] & [Berikutnya / Lanjut Uraian]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (currentMcqIndex > 0) currentMcqIndex-- },
                            enabled = currentMcqIndex > 0,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_mcq_prev")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sebelumnya")
                        }

                        Button(
                            onClick = {
                                if (currentMcqIndex < mcqs.lastIndex) {
                                    currentMcqIndex++
                                } else {
                                    activeEvalPart = 1
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_mcq_next")
                        ) {
                            Text(if (currentMcqIndex < mcqs.lastIndex) "Berikutnya" else "Lanjut Soal Uraian")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        } else {
            // BAGIAN B: 4 SOAL URAIAN & MODE PENILAIAN (Bagian V & W)
            essays.forEach { eq ->
                AutosaveAnswerCard(
                    questionId = eq.id,
                    numberLabel = eq.categoryTitle,
                    questionText = eq.question,
                    savedAnswer = answersMap[eq.id]?.answer ?: "",
                    aiFeedback = answersMap[eq.id]?.aiFeedback ?: "",
                    warningNote = "Fokus Rubrik: ${eq.rubricFocus}",
                    isAiEnabled = config.isAiEnabled,
                    onSaveAnswer = {
                        onSaveAnswer(eq.id, it)
                        showValidationError = null
                    },
                    onRequestAiFeedback = {
                        onRequestAiFeedback(eq.id, eq.question, eq.rubricFocus)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Konfigurasi Mode Penilaian Uraian (MODE GURU vs MODE AI - Bagian W)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sistem Penilaian Soal Uraian",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pilih bagaimana jawaban soal uraian Anda akan dievaluasi:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !useAiGradingMode,
                            onClick = { useAiGradingMode = false },
                            label = { Text("MODE GURU (Manual)") },
                            modifier = Modifier.testTag("chip_mode_guru")
                        )
                        if (config.isAiEnabled) {
                            FilterChip(
                                selected = useAiGradingMode,
                                onClick = { useAiGradingMode = true },
                                label = { Text("MODE AI (Bantuan Rubrik)") },
                                leadingIcon = {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier.testTag("chip_mode_ai")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (!useAiGradingMode) {
                            "• MODE GURU: Nilai Pilihan Ganda dihitung langsung, sedangkan skor Soal Uraian menunggu penilaian manual oleh Guru melalui Portal Guru."
                        } else {
                            "• MODE AI: Penilaian AI bersifat bantuan berdasarkan 6 aspek rubrik dan dapat ditinjau kembali atau diubah oleh Guru."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (showValidationError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = showValidationError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val missingMcq = mcqs.filter { answersMap[it.id]?.answer.isNullOrBlank() }
                            val missingEssay = essays.filter { answersMap[it.id]?.answer.isNullOrBlank() }
                            if (missingMcq.isNotEmpty()) {
                                showValidationError = "Masih ada ${missingMcq.size} soal Pilihan Ganda yang belum dijawab (Nomor: ${missingMcq.joinToString { it.number.toString() }})."
                                return@Button
                            }
                            if (missingEssay.isNotEmpty()) {
                                showValidationError = "Mohon lengkapi seluruh 4 soal Uraian sebelum menekan Submit Evaluasi."
                                return@Button
                            }
                            onSubmitEvaluation(useAiGradingMode)
                        },
                        enabled = !isAiLoading,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("btn_submit_evaluasi")
                    ) {
                        if (isAiLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Memproses Evaluasi & Rubrik...")
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit / Finish Evaluasi E-LKPD",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = { Text("Keluar dari Halaman Evaluasi?") },
            text = {
                Text("Jawaban yang sudah Anda isi telah tersimpan otomatis (Autosave). Apakah Anda ingin kembali ke Beranda?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmDialog = false
                        onBackToHome()
                    }
                ) {
                    Text("Ya, Kembali ke Beranda")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitConfirmDialog = false }) {
                    Text("Lanjutkan Evaluasi")
                }
            }
        )
    }
}

/**
 * LAYAR HASIL SAYA (Bagian X, Y, AK)
 * Menampilkan rincian nilai, status penilaian, rubrik 6 aspek, riwayat pengerjaan,
 * serta 4 aksi wajib: [Ulangi Evaluasi] [Lihat Jawaban] [Simpan Hasil] [Bagikan Hasil]
 */
@Composable
fun ResultScreen(
    activeUser: UserEntity?,
    config: TeacherConfigEntity,
    progressList: List<ProgressEntity>,
    answersMap: Map<String, AnswerEntity>,
    resultsHistory: List<ResultEntity>,
    onRetakeEvaluation: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var showAnswersReview by remember { mutableStateOf(false) }
    val latestResult = resultsHistory.firstOrNull()
    val totalProgress = if (progressList.isEmpty()) 0 else progressList.sumOf { it.progress } / 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "HASIL EVALUASI & LAPORAN E-LKPD",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(10.dp))

                val dateFormatted = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                    .format(Date(latestResult?.date ?: System.currentTimeMillis()))

                ResultDetailRow("Nama", activeUser?.nama?.ifBlank { "-" } ?: "-")
                ResultDetailRow("NIS", activeUser?.nis?.ifBlank { "-" } ?: "-")
                ResultDetailRow("Kelas", activeUser?.kelas ?: "-")
                ResultDetailRow("Kelompok", "${activeUser?.kelompok ?: "-"} (${activeUser?.modeKerja ?: "KELOMPOK"})")
                ResultDetailRow("Tanggal", dateFormatted)
                ResultDetailRow("Progress E-LKPD", "$totalProgress%")

                Spacer(modifier = Modifier.height(10.dp))

                if (latestResult != null) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            ResultDetailRow(
                                "Nilai Pilihan Ganda",
                                "${String.format(Locale.US, "%.1f", latestResult.mcqScore)} (${latestResult.mcqCorrectCount}/${latestResult.mcqTotalCount} Benar)"
                            )
                            ResultDetailRow(
                                "Nilai Uraian",
                                latestResult.essayScore?.let { String.format(Locale.US, "%.1f", it) }
                                    ?: "Menunggu Penilaian Guru"
                            )
                            ResultDetailRow(
                                "Nilai Akhir",
                                String.format(Locale.US, "%.1f", latestResult.finalScore),
                                isHighlight = true
                            )
                            ResultDetailRow("Status", latestResult.gradingMode)

                            if (latestResult.gradingMode == "DINILAI_AI") {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "“Penilaian AI bersifat bantuan dan dapat ditinjau kembali oleh guru.”",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            if (latestResult.teacherFeedback.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Catatan Evaluasi: ${latestResult.teacherFeedback}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Anda belum mengirimkan Evaluasi akhir. Silakan kerjakan menu Uji Kemampuan atau simpan laporan aktivitas sementara.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4 Tombol Aksi sesuai Bagian X: [Ulangi Evaluasi] [Lihat Jawaban] [Simpan Hasil] [Bagikan Hasil]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetakeEvaluation,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_ulangi_evaluasi")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ulangi Evaluasi")
                    }

                    OutlinedButton(
                        onClick = { showAnswersReview = !showAnswersReview },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_lihat_jawaban")
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showAnswersReview) "Tutup Jawaban" else "Lihat Jawaban")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val user = activeUser ?: UserEntity(
                                nama = "Peserta Didik",
                                nis = "-",
                                kelas = "X Fase E",
                                kelompok = "Kelompok 1"
                            )
                            val pdfFile = PdfExportGenerator.generateAndShareStudentReportPdf(
                                context = context,
                                user = user,
                                config = config,
                                progressList = progressList,
                                answersMap = answersMap,
                                latestResult = latestResult
                            )
                            if (pdfFile != null) {
                                onShowMessage("PDF berhasil disimpan: ${pdfFile.name}")
                            } else {
                                onShowMessage("Gagal membuat PDF laporan.")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_simpan_hasil_pdf")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Hasil (PDF)")
                    }

                    Button(
                        onClick = {
                            val user = activeUser ?: UserEntity(
                                nama = "Peserta Didik",
                                nis = "-",
                                kelas = "X Fase E",
                                kelompok = "Kelompok 1"
                            )
                            val pdfFile = PdfExportGenerator.generateAndShareStudentReportPdf(
                                context = context,
                                user = user,
                                config = config,
                                progressList = progressList,
                                answersMap = answersMap,
                                latestResult = latestResult
                            )
                            if (pdfFile != null) {
                                PdfExportGenerator.sharePdfFile(context, pdfFile)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_bagikan_hasil_pdf")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bagikan Hasil")
                    }
                }
            }
        }

        // Tampilan Review Jawaban & Pembahasan Kunci Jawaban (Hanya tampil setelah evaluasi selesai)
        AnimatedVisibility(visible = showAnswersReview) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Rekaman Jawaban & Pembahasan Evaluasi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ElpkdCurriculumData.defaultMcqQuestions.forEach { q ->
                            val studentChoice = answersMap[q.id]?.answer ?: "-"
                            val isCorrect = studentChoice == q.correctOption
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Soal PG ${q.number}: Jawaban Anda [$studentChoice]",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                    // Jangan menampilkan kunci jawaban sebelum peserta menyelesaikan evaluasi (Bagian U)
                                    if (latestResult != null) {
                                        Text(
                                            text = "Kunci Jawaban: ${q.correctOption} — ${q.explanation}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    } else {
                                        Text(
                                            text = "Kunci jawaban akan terbuka setelah Anda menekan Submit Evaluasi.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        ElpkdCurriculumData.defaultEssayQuestions.forEach { eq ->
                            val ans = answersMap[eq.id]
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = eq.categoryTitle,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = ans?.answer?.ifBlank { "(Belum dijawab)" } ?: "(Belum dijawab)",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (!ans?.aiFeedback.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = ans?.aiFeedback ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BAGIAN Y: RUBRIK PENILAIAN 6 ASPEK (Skala 1–4)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Rubrik Penilaian Soal Uraian (Skala 1–4)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bobot masing-masing aspek dapat disesuaikan oleh Guru pada Pengaturan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                val rubricItems = listOf(
                    Triple("1. Ketepatan Konsep Biologi", "${config.weightKonsepBiologi}%", "Akurasi konsep keanekaragaman spesies, morfologi, dan ekologi Medinilla speciosa."),
                    Triple("2. Penggunaan Data", "${config.weightPenggunaanData}%", "Pemanfaatan data riset metabolit sekunder (flavonoid, saponin, tanin, β-sitosterol)."),
                    Triple("3. Kemampuan Analisis", "${config.weightKemampuanAnalisis}%", "Ketajaman menghubungkan fenomena etnosains dengan dampak ekologis hutan Muria."),
                    Triple("4. Berpikir Kritis", "${config.weightBerpikirKritis}%", "Evaluasi kritis atas konflik ekonomi vs konservasi serta status Indikasi Geografis."),
                    Triple("5. Solusi Konservasi", "${config.weightSolusiKonservasi}%", "Inovasi dan kelayakan strategi budidaya mandiri (ex-situ) ramah lingkungan."),
                    Triple("6. Kemampuan Komunikasi", "${config.weightKomunikasi}%", "Sistematika bahasa ilmiah dan efektivitas pesan kampanye literasi konservasi.")
                )

                rubricItems.forEach { (title, weight, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = weight,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // RIWAYAT PENGERJAAN (Bagian B.21)
        if (resultsHistory.size > 1) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Riwayat Pengerjaan Evaluasi (${resultsHistory.size} Percobaan)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    resultsHistory.forEachIndexed { idx, res ->
                        val tgl = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(res.date))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "#${resultsHistory.size - idx} • $tgl (${res.gradingMode})",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Nilai Akhir: ${String.format(Locale.US, "%.1f", res.finalScore)}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ResultDetailRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
        )
    }
}
