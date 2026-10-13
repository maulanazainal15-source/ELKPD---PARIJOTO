package com.example.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentEditorDialog(
    initialContent: DynamicContent?,
    defaultCategory: ContentCategory?,
    onDismiss: () -> Unit,
    onSave: (id: String?, title: String, content: String, category: ContentCategory, subCategory: String, orderIndex: Int, author: String) -> Unit
) {
    val isEditMode = initialContent != null

    var selectedCategory by remember {
        mutableStateOf(initialContent?.category ?: defaultCategory ?: ContentCategory.GUIDE)
    }
    var subCategory by remember {
        mutableStateOf(initialContent?.subCategory ?: selectedCategory.getCommonSubCategories().firstOrNull()?.first ?: "UMUM")
    }
    var title by remember { mutableStateOf(initialContent?.title ?: "") }
    var contentText by remember { mutableStateOf(initialContent?.content ?: "") }
    var orderIndex by remember { mutableIntStateOf(initialContent?.orderIndex ?: 1) }
    var author by remember { mutableStateOf(initialContent?.author ?: "Ahzami, S.Pd., Gr") }

    var titleTouched by remember { mutableStateOf(false) }
    var contentTouched by remember { mutableStateOf(false) }

    val isTitleValid = title.trim().isNotBlank()
    val isContentValid = contentText.trim().isNotBlank()
    val canSubmit = isTitleValid && isContentValid

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isEditMode) Icons.Default.Edit else Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isEditMode) "Edit Konten E-LKPD" else "Tambah Konten Baru",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Modul CMS Dinamis Etnosains Parijoto",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Kategori Konten
                Text(
                    text = "Kategori Utama*",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ContentCategory.entries.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategory = cat
                                subCategory = cat.getCommonSubCategories().firstOrNull()?.first ?: "UMUM"
                            },
                            label = {
                                Text(
                                    text = cat.badge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Sub-Kategori / Topik
                Text(
                    text = "Pilih Sub-Kategori / Topik*",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    selectedCategory.getCommonSubCategories().forEach { (key, label) ->
                        val isSelected = subCategory == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { subCategory = key },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Judul Konten
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleTouched = true
                    },
                    label = { Text("Judul Konten / Modul*") },
                    placeholder = { Text("Contoh: Modul Ekstraksi Antosianin Parijoto") },
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                    isError = titleTouched && !isTitleValid,
                    supportingText = {
                        if (titleTouched && !isTitleValid) {
                            Text("Judul tidak boleh kosong atau hanya spasi", color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("${title.length} karakter")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cms_input_title"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 4. Penulis & Nomor Urut Tampilan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Nama Penulis / Penyusun") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = orderIndex.toString(),
                        onValueChange = { orderIndex = it.toIntOrNull() ?: 0 },
                        label = { Text("Urutan") },
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Isi Teks Materi / Panduan
                OutlinedTextField(
                    value = contentText,
                    onValueChange = {
                        contentText = it
                        contentTouched = true
                    },
                    label = { Text("Isi Teks Materi / Panduan Lengkap*") },
                    placeholder = { Text("Tuliskan deskripsi materi, langkah-langkah praktikum, atau rincian instruksi di sini...") },
                    minLines = 6,
                    maxLines = 14,
                    isError = contentTouched && !isContentValid,
                    supportingText = {
                        if (contentTouched && !isContentValid) {
                            Text("Teks konten tidak boleh kosong!", color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("${contentText.length} karakter • Mendukung multi-paragraf & poin penomoran")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cms_input_content"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 6. Live Preview Card (Pratinjau Tampilan Siswa)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Pratinjau Tampilan di Layar Siswa",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = selectedCategory.badge.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (title.isNotBlank()) title else "Judul Konten Belum Diisi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (title.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (contentText.isNotBlank()) contentText else "Isi konten materi/panduan akan dirender di sini secara rapi dan proporsional.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (contentText.isNotBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            titleTouched = true
                            contentTouched = true
                            if (canSubmit) {
                                onSave(
                                    initialContent?.id,
                                    title,
                                    contentText,
                                    selectedCategory,
                                    subCategory,
                                    orderIndex,
                                    author
                                )
                            }
                        },
                        enabled = canSubmit,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("cms_btn_save_content"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEditMode) "Perbarui Konten" else "Simpan Konten",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
