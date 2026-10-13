package com.example.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.StudentSubmission
import com.example.domain.model.SubmissionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentSubmissionsSection(
    state: TeacherDashboardState,
    onFilterSelect: (SubmissionType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onViewDetailClick: (StudentSubmission) -> Unit,
    onStatusChangeClick: (String, String) -> Unit,
    onDeleteClick: (StudentSubmission) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // KPI Summary Submisi
        item {
            SubmissionsKpiCard(
                totalSubmissions = state.submissions.size,
                act1Count = state.submissions.count { it.submissionType == SubmissionType.ACTIVITY_1 },
                act2Count = state.submissions.count { it.submissionType == SubmissionType.ACTIVITY_2 },
                evalCount = state.submissions.count { it.submissionType == SubmissionType.EVALUATION_HOTS },
                surveyCount = state.submissions.count { it.submissionType == SubmissionType.USABILITY_SURVEY }
            )
        }

        // Pencarian Siswa / Submisi
        item {
            OutlinedTextField(
                value = state.submissionSearchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cari berdasarkan Nama Siswa, NIS, atau Kelas...") },
                placeholder = { Text("Contoh: Siti, X E-1, 1024") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.submissionSearchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Filter Jenis Submisi
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.submissionFilterType == null,
                    onClick = { onFilterSelect(null) },
                    label = { Text("Semua (${state.submissions.size})") }
                )
                SubmissionType.entries.forEach { type ->
                    val count = state.submissions.count { it.submissionType == type }
                    FilterChip(
                        selected = state.submissionFilterType == type,
                        onClick = { onFilterSelect(type) },
                        label = { Text("${type.displayName.take(18)}... ($count)") }
                    )
                }
            }
        }

        // List Submisi Jawaban Siswa
        if (state.filteredSubmissions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Belum Ada Submisi Jawaban Masuk",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Jawaban siswa dari Aktivitas 1, Aktivitas 2, Evaluasi HOTS, dan Survei Usabilitas akan otomatis masuk ke sini secara real-time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(state.filteredSubmissions, key = { it.id }) { submission ->
                SubmissionCardItem(
                    submission = submission,
                    onViewDetail = { onViewDetailClick(submission) },
                    onMarkReviewed = {
                        val nextStatus = if (submission.status == "TERPERIKSA") "TERKIRIM" else "TERPERIKSA"
                        onStatusChangeClick(submission.id, nextStatus)
                    },
                    onDelete = { onDeleteClick(submission) }
                )
            }
        }
    }
}

@Composable
private fun SubmissionsKpiCard(
    totalSubmissions: Int,
    act1Count: Int,
    act2Count: Int,
    evalCount: Int,
    surveyCount: Int
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                Column {
                    Text(
                        text = "Rekapitulasi Respon & Jawaban Masuk",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Total $totalSubmissions berkas jawaban dikirimkan secara otomatis",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "$totalSubmissions Respon",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiMiniItem(title = "E-LKPD 1", count = act1Count, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                KpiMiniItem(title = "E-LKPD 2", count = act2Count, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
                KpiMiniItem(title = "HOTS Test", count = evalCount, color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
                KpiMiniItem(title = "Survei SUS", count = surveyCount, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun KpiMiniItem(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SubmissionCardItem(
    submission: StudentSubmission,
    onViewDetail: () -> Unit,
    onMarkReviewed: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val formattedDate = dateFormat.format(Date(submission.submittedAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Identitas Siswa & Waktu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = submission.studentName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Kelas: ${submission.studentClass} • NIS: ${submission.studentNis}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = if (submission.status == "TERPERIKSA") MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (submission.status == "TERPERIKSA") "Diperiksa ✓" else "Terkirim",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = if (submission.status == "TERPERIKSA") MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Aktivitas & Judul
            Surface(
                color = when (submission.submissionType) {
                    SubmissionType.ACTIVITY_1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    SubmissionType.ACTIVITY_2 -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                    SubmissionType.EVALUATION_HOTS -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
                    SubmissionType.USABILITY_SURVEY -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (submission.submissionType) {
                            SubmissionType.ACTIVITY_1 -> Icons.Default.Explore
                            SubmissionType.ACTIVITY_2 -> Icons.Default.Biotech
                            SubmissionType.EVALUATION_HOTS -> Icons.Default.Assignment
                            SubmissionType.USABILITY_SURVEY -> Icons.Default.RateReview
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = submission.submissionType.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = submission.summary,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Terkirim pada $formattedDate",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Periksa Detail", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = onMarkReviewed,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (submission.status == "TERPERIKSA") "Batal Cek" else "Tandai Cek",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
