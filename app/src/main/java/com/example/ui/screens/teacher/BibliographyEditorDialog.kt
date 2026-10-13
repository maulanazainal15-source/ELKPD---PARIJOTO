package com.example.ui.screens.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.BibliographyCategory
import com.example.domain.model.BibliographyItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibliographyEditorDialog(
    initialItem: BibliographyItem?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        authors: String,
        year: String,
        source: String,
        category: BibliographyCategory,
        doiOrUrl: String,
        description: String
    ) -> Unit
) {
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var authors by remember { mutableStateOf(initialItem?.authors ?: "") }
    var year by remember { mutableStateOf(initialItem?.year ?: "2024") }
    var source by remember { mutableStateOf(initialItem?.source ?: "") }
    var selectedCategory by remember { mutableStateOf(initialItem?.category ?: BibliographyCategory.JURNAL) }
    var doiOrUrl by remember { mutableStateOf(initialItem?.doiOrUrl ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialItem != null) "Edit Referensi Pustaka" else "Tambah Referensi Baru",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Judul Referensi
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Karya / Artikel / Buku *") },
                    placeholder = { Text("Contoh: Karakterisasi Antosianin Parijoto") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Penulis
                OutlinedTextField(
                    value = authors,
                    onValueChange = { authors = it },
                    label = { Text("Nama Penulis (Format APA) *") },
                    placeholder = { Text("Contoh: Rahayu, S., Hartono, M., & Wulandari, E.") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tahun
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Tahun Terbit") },
                        modifier = Modifier.weight(1f)
                    )

                    // Kategori Dropdown
                    ExposedDropdownMenuBox(
                        expanded = isCategoryDropdownExpanded,
                        onExpandedChange = { isCategoryDropdownExpanded = it },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.chipLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = isCategoryDropdownExpanded,
                            onDismissRequest = { isCategoryDropdownExpanded = false }
                        ) {
                            BibliographyCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName) },
                                    onClick = {
                                        selectedCategory = cat
                                        isCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sumber / Jurnal / Penerbit
                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("Penerbit / Nama Jurnal / Instansi") },
                    placeholder = { Text("Contoh: Jurnal Riset Kimia, 14(2), 112-123") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tautan DOI / URL
                OutlinedTextField(
                    value = doiOrUrl,
                    onValueChange = { doiOrUrl = it },
                    label = { Text("Tautan URL / DOI / ISBN") },
                    placeholder = { Text("https://doi.org/10... atau ISBN 978...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Deskripsi Singkat
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi / Abstrak Singkat") },
                    placeholder = { Text("Keterkaitan dengan materi Parijoto...") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            onSave(
                                title,
                                authors,
                                year,
                                source,
                                selectedCategory,
                                doiOrUrl,
                                description
                            )
                        },
                        enabled = title.isNotBlank() && authors.isNotBlank(),
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan")
                    }
                }
            }
        }
    }
}
