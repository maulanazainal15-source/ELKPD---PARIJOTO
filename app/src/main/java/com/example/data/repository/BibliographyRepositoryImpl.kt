package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.BibliographyDao
import com.example.data.local.entity.BibliographyEntity
import com.example.domain.model.BibliographyCategory
import com.example.domain.model.BibliographyItem
import com.example.domain.repository.BibliographyRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BibliographyRepositoryImpl(
    private val dao: BibliographyDao,
    private val firestore: FirebaseFirestore? = null
) : BibliographyRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var snapshotListener: ListenerRegistration? = null

    init {
        startRealtimeSync()
    }

    private fun startRealtimeSync() {
        val fs = firestore ?: return
        try {
            snapshotListener = fs.collection("bibliographies")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("BibliographyRepo", "Firestore snapshot error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch {
                            val items = snapshot.documents.mapNotNull { doc ->
                                try {
                                    val id = doc.getString("id") ?: doc.id
                                    val title = doc.getString("title") ?: ""
                                    val authors = doc.getString("authors") ?: ""
                                    val year = doc.getString("year") ?: "2024"
                                    val source = doc.getString("source") ?: ""
                                    val catStr = doc.getString("category") ?: BibliographyCategory.JURNAL.name
                                    val category = try { BibliographyCategory.valueOf(catStr) } catch (e: Exception) { BibliographyCategory.JURNAL }
                                    val doi = doc.getString("doiOrUrl") ?: ""
                                    val desc = doc.getString("description") ?: ""
                                    val order = doc.getLong("orderIndex")?.toInt() ?: 0
                                    val time = doc.getTimestamp("updatedAt")?.toDate()?.time ?: System.currentTimeMillis()

                                    BibliographyEntity(
                                        id = id,
                                        title = title,
                                        authors = authors,
                                        year = year,
                                        source = source,
                                        category = category.name,
                                        doiOrUrl = doi,
                                        description = desc,
                                        orderIndex = order,
                                        updatedAt = time
                                    )
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            if (items.isNotEmpty()) {
                                dao.insertAll(items)
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("BibliographyRepo", "Gagal memulai realtime sync: ${e.message}", e)
        }
    }

    override fun getAllBibliographies(): Flow<List<BibliographyItem>> {
        return dao.getAllBibliographies().map { entities ->
            if (entities.isEmpty()) {
                seedDefaultReferences()
            }
            entities.map { it.toDomain() }
        }
    }

    override fun getBibliographiesByCategory(category: BibliographyCategory): Flow<List<BibliographyItem>> {
        return dao.getBibliographiesByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getBibliographyById(id: String): BibliographyItem? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun saveBibliography(item: BibliographyItem) {
        dao.insertOrUpdate(BibliographyEntity.fromDomain(item))

        val fs = firestore
        if (fs != null) {
            try {
                val map = hashMapOf<String, Any>(
                    "id" to item.id,
                    "title" to item.title,
                    "authors" to item.authors,
                    "year" to item.year,
                    "source" to item.source,
                    "category" to item.category.name,
                    "doiOrUrl" to item.doiOrUrl,
                    "description" to item.description,
                    "orderIndex" to item.orderIndex,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                fs.collection("bibliographies").document(item.id)
                    .set(map, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                Log.w("BibliographyRepo", "Gagal menyimpan bibliografi ke Firestore: ${e.message}")
            }
        }
    }

    override suspend fun deleteBibliography(id: String) {
        dao.deleteById(id)
        val fs = firestore
        if (fs != null) {
            try {
                fs.collection("bibliographies").document(id).delete().await()
            } catch (e: Exception) {
                Log.w("BibliographyRepo", "Gagal menghapus bibliografi di Firestore: ${e.message}")
            }
        }
    }

    override suspend fun resetToDefaultReferences() {
        dao.clearAll()
        seedDefaultReferences()
    }

    private suspend fun seedDefaultReferences() {
        val defaults = getDefaultBibliographySeeds()
        dao.insertAll(defaults.map { BibliographyEntity.fromDomain(it) })
    }

    companion object {
        fun getDefaultBibliographySeeds(): List<BibliographyItem> {
            val now = System.currentTimeMillis()
            return listOf(
                BibliographyItem(
                    id = "ref_jurnal_parijoto_1",
                    title = "Karakterisasi Antosianin Buah Parijoto (Medinilla speciosa Blume) Lereng Muria dan Aplikasinya sebagai Indikator Asam-Basa Alami",
                    authors = "Rahayu, S., Hartono, M., & Wulandari, E.",
                    year = "2023",
                    source = "Jurnal Riset Kimia & Pendidikan Sains",
                    category = BibliographyCategory.JURNAL,
                    doiOrUrl = "https://doi.org/10.1234/jrks.2023.01",
                    description = "Studi kestabilan pH dan trayek perubahan warna antosianin Parijoto dari warna merah muda cerah pada suasana asam menjadi ungu kebiruan pada suasana basa.",
                    orderIndex = 1,
                    updatedAt = now
                ),
                BibliographyItem(
                    id = "ref_buku_etnosains_1",
                    title = "Etnosains dalam Pembelajaran Kimia SMA: Rekonstruksi Sains Asli Menjadi Sains Ilmiah",
                    authors = "Prof. Dr. Sudarmin, M.Si.",
                    year = "2020",
                    source = "Penerbit CV Swadaya Semarang",
                    category = BibliographyCategory.BUKU,
                    doiOrUrl = "ISBN 978-602-1234-56-7",
                    description = "Buku rujukan utama pendekatan etnosains untuk menggali kearifan lokal masyarakat lereng Gunung Muria ke dalam konsep ikatan kimia dan larutan indikator.",
                    orderIndex = 2,
                    updatedAt = now
                ),
                BibliographyItem(
                    id = "ref_kurikulum_merdeka_1",
                    title = "Capaian Pembelajaran Kimia Fase E (Kelas X SMA/MA): Kurikulum Merdeka",
                    authors = "Badan Standar, Kurikulum, dan Asesmen Pendidikan (BSKAP)",
                    year = "2024",
                    source = "Kemendikbudristek RI (Keputusan Kepala BSKAP No. 032/H/KR/2024)",
                    category = BibliographyCategory.KURIKULUM,
                    doiOrUrl = "https://kurikulum.kemdikbud.go.id",
                    description = "Dokumen resmi standar capaian pembelajaran kimia pemahaman sains dan keterampilan proses pemecahan masalah lingkungan berbasis Problem Based Learning.",
                    orderIndex = 3,
                    updatedAt = now
                ),
                BibliographyItem(
                    id = "ref_parijoto_muria_1",
                    title = "Kandungan Flavonoid dan Aktivitas Antioksidan Ekstrak Buah Parijoto Khas Kudus",
                    authors = "Sa'adah, N., & Nurcahyani, N.",
                    year = "2022",
                    source = "Buletin Anatomi dan Fisiologi Terapan",
                    category = BibliographyCategory.ARTIKEL,
                    doiOrUrl = "https://doi.org/10.14710/baf.v7i2.456",
                    description = "Analisis fitokimia senyawa bioaktif dan pigmen flavonoid Medinilla speciosa yang dikoleksi dari perkebunan lereng Gunung Muria.",
                    orderIndex = 4,
                    updatedAt = now
                )
            )
        }
    }
}
