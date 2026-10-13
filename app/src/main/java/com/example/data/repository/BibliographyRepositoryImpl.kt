package com.example.data.repository

import com.example.data.local.dao.BibliographyDao
import com.example.data.local.entity.BibliographyEntity
import com.example.domain.model.BibliographyCategory
import com.example.domain.model.BibliographyItem
import com.example.domain.repository.BibliographyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BibliographyRepositoryImpl(
    private val dao: BibliographyDao
) : BibliographyRepository {

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
    }

    override suspend fun deleteBibliography(id: String) {
        dao.deleteById(id)
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
                    source = "Jurnal Riset Kimia Indonesia, 14(2), 112-123",
                    category = BibliographyCategory.JURNAL,
                    doiOrUrl = "https://doi.org/10.1016/j.jrk.2023.04.012",
                    description = "Penelitian komprehensif spektrum absorbansi antosianin Parijoto pada rentang pH 1 hingga 14 dengan pergeseran warna merah muda hingga kuning kehijauan.",
                    orderIndex = 1,
                    updatedAt = now
                ),
                BibliographyItem(
                    id = "ref_jurnal_etnosains_2",
                    title = "Rekonstruksi Sains Asli (Indigenous Science) Masyarakat Lereng Gunung Muria Kudus ke dalam Pembelajaran Kimia Larutan SMA",
                    authors = "Prasetyo, A. B., Sudarmin, S., & Masturi, M.",
                    year = "2022",
                    source = "Indonesian Journal of Science Education, 11(3), 245-258",
                    category = BibliographyCategory.JURNAL,
                    doiOrUrl = "https://doi.org/10.15294/jise.v11i3.54120",
                    description = "Eksplorasi etnosains budidaya, mitos Sunan Muria, serta transformasi pengetahuan lokal menjadi konsep kimia asam basa dan larutan penyangga.",
                    orderIndex = 2,
                    updatedAt = now
                ),
                BibliographyItem(
                    id = "ref_jurnal_antioksidan_3",
                    title = "Uji Aktivitas Antioksidan dan Kandungan Total Fenolik Ekstrak Buah Parijoto Berbantu Ultrasonik (UAE)",
                    authors = "Nugroho, F. A., & Kusuma, D. W.",
                    year = "2021",
                    source = "Jurnal Farmasi & Sains Terapan, 8(1), 45-53",
                    category = BibliographyCategory.JURNAL,
                    doiOrUrl = "https://doi.org/10.22146/jfst.2021.01.004",
                    description = "Analisis senyawa flavonoid, antosianin pelargonidin, dan sianidin glukosida pada buah Parijoto serta stabilitas pigmen terhadap variasi suhu.",
                    orderIndex = 3,
                    updatedAt = now
                ),
                BibliographyItem(
                    id = "ref_buku_kimia_sma_4",
                    title = "Kimia untuk SMA/MA Kelas XI Berorientasi Pendekatan Etnosains dan Kurikulum Merdeka",
                    authors = "Sudarmin, S., & Tim Pengembang Kurikulum Etnosains",
                    year = "2023",
                    source = "Semarang: Penerbit LPPM Universitas Negeri Semarang",
                    category = BibliographyCategory.BUKU,
                    doiOrUrl = "ISBN 978-623-645-120-4",
                    description = "Buku teks ajar rujukan resmi sintaks Problem-Based Learning berbantuan indikator alami kearifan lokal Jawa Tengah.",
                    orderIndex = 4,
                    updatedAt = now
                ),
                BibliographyItem(
                    id = "ref_buku_muria_5",
                    title = "Flora Pegunungan Muria dan Konservasi Kearifan Lokal Sunan Muria",
                    authors = "Sutrisno, E., & Handayani, T.",
                    year = "2020",
                    source = "Kudus: Muria Heritage Press",
                    category = BibliographyCategory.BUKU,
                    doiOrUrl = "ISBN 978-602-512-301-9",
                    description = "Inventarisasi taksonomi botani Medinilla speciosa, ekologi tanah vulkanik lereng Gunung Muria, dan nilai sosio-kultural masyarakat setempat.",
                    orderIndex = 5,
                    updatedAt = now
                ),
                BibliographyItem(
                    id = "ref_kurikulum_merdeka_6",
                    title = "Capaian Pembelajaran dan Alur Tujuan Pembelajaran Mata Pelajaran Kimia Fase F",
                    authors = "Badan Standar, Kurikulum, dan Asesmen Pendidikan (BSKAP)",
                    year = "2022",
                    source = "Jakarta: Kementerian Pendidikan, Kebudayaan, Riset, dan Teknologi",
                    category = BibliographyCategory.KURIKULUM,
                    doiOrUrl = "https://kurikulum.kemdikbud.go.id/bskap-033",
                    description = "Keputusan Kepala BSKAP No. 033/H/KR/2022 tentang Capaian Pembelajaran pada Pendidikan Anak Usia Dini, Jenjang Pendidikan Dasar, dan Menengah.",
                    orderIndex = 6,
                    updatedAt = now
                ),
                BibliographyItem(
                    id = "ref_artikel_kudus_7",
                    title = "Parijoto: Simbol Berkah dan Obat Alami dari Lereng Gunung Muria Kudus",
                    authors = "Pusat Studi Kebudayaan Jawa Muria",
                    year = "2024",
                    source = "Portal Etnosains Budaya Nusantara, Artikel No. 88",
                    category = BibliographyCategory.ARTIKEL,
                    doiOrUrl = "https://etnosains.kemdikbud.go.id/parijoto-muria",
                    description = "Kajian folklor dan tradisi ibu hamil mengonsumsi buah Parijoto serta telaah fitokimia modern.",
                    orderIndex = 7,
                    updatedAt = now
                )
            )
        }
    }
}
