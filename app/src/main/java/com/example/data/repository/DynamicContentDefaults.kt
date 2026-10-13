package com.example.data.repository

import com.example.domain.model.ContentCategory
import com.example.domain.model.DynamicContent

object DynamicContentDefaults {

    fun getDefaultContents(): List<DynamicContent> {
        val now = System.currentTimeMillis()
        return listOf(
            // ==========================================
            // KATEGORI 1: PANDUAN & PETUNJUK (GUIDE)
            // ==========================================
            DynamicContent(
                id = "guide_siswa_digital",
                category = ContentCategory.GUIDE,
                subCategory = "PANDUAN_SISWA_GURU",
                title = "Panduan Penggunaan E-LKPD Digital untuk Siswa",
                content = """
                    1. Membaca wacana stimulus dan informasi saintifik etnosains dengan seksama.
                    2. Mengamati video dokumenter lapangan Gunung Muria dan scan QR Code literasi pendukung.
                    3. Berdiskusi secara kolaboratif bersama kelompok (atau berefleksi pada mode mandiri).
                    4. Mengisi seluruh jawaban analisis pada kolom input interaktif yang dilengkapi fitur penyimpanan otomatis (autosave).
                    5. Mengunggah foto morfologi tanaman Parijoto melalui kamera atau galeri beserta deskripsi hasil pengamatan.
                    6. Menyelesaikan seluruh tahapan: Pendahuluan, Aktivitas 1, Aktivitas 2, hingga Evaluasi HOTS.
                    7. Menekan tombol Simpan/Submit dan mengunduh laporan PDF Hasil Pembelajaran / Rapor Digital.
                """.trimIndent(),
                orderIndex = 1,
                author = "Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "guide_guru_admin",
                category = ContentCategory.GUIDE,
                subCategory = "PANDUAN_SISWA_GURU",
                title = "Panduan Operasional Dashboard Guru & Administrator",
                content = """
                    1. Memantau progres belajar dan analitik HOTS seluruh siswa secara kolektif maupun individual melalui tab Analitik & Siswa.
                    2. Menyaring siswa yang memerlukan intervensi bimbingan/remedial berdasarkan batas KKM dinamis.
                    3. Mengatur kebijakan otorisasi, PIN guru/admin, dan mode akses aplikasi pada tab Akses & Kebijakan.
                    4. Menyesuaikan parameter KKM (55-90 poin), batas waktu evaluasi, dan status AI Tutor pada tab Konten & KKM.
                    5. Mengelola akun siswa dan guru (tambah, hapus, alih peran) pada tab Kelola Akun.
                    6. Melakukan manajemen konten kurikulum, panduan K3, materi sintaks PBL, dan langkah praktikum melalui modul CMS internal ini secara dinamis tanpa re-compile aplikasi.
                    7. Mengekspor rekapitulasi nilai kelas ke format CSV/Excel untuk pengarsipan rapor sekolah.
                """.trimIndent(),
                orderIndex = 2,
                author = "Tim Pengembang Kurikulum Etnosains",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "guide_k3_laboratorium",
                category = ContentCategory.GUIDE,
                subCategory = "KESELAMATAN_K3",
                title = "Petunjuk Keselamatan Kerja Laboratorium (K3 Etnosains)",
                content = """
                    1. Wajib mengenakan jas praktikum, masker, sarung tangan lateks, dan kacamata pengaman (goggles) selama kegiatan preparasi sampel.
                    2. Berhati-hati saat menggunakan mortar, alu, dan tabung reaksi kaca untuk mencegah risiko pecahan kaca.
                    3. Penanganan Reagen Kimia: Pengujian keasaman/kebasaan menggunakan larutan asam (HCl/asam cuka) dan basa (NaOH/air kapur) harus dilakukan secara terukur menggunakan pipet tetes. Jangan mencium aroma uap secara langsung.
                    4. Kontak Darurat: Jika reagen mengenai kulit atau mata, segera basuh dengan air mengalir selama minimal 15 menit dan laporkan kepada guru pembimbing.
                    5. Pengelolaan Limbah: Pisahkan limbah ampas organik buah Parijoto dengan limbah larutan kimia pada wadah pembuangan yang telah disediakan.
                    6. Kebersihan & Kerapian: Pastikan area meja praktikum, alat gelas, dan perangkat ponsel bersih serta kering sebelum meninggalkan laboratorium.
                """.trimIndent(),
                orderIndex = 3,
                author = "Koordinator Lab Kimia & Biologi",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "guide_fitur_interaktif",
                category = ContentCategory.GUIDE,
                subCategory = "FITUR_INTERAKTIF",
                title = "Petunjuk Penggunaan Fitur Interaktif (AR, Quiz Duel, Forum)",
                content = """
                    1. Virtual Lab & Augmented Reality (AR):
                       - Buka menu AR Parijoto, arahkan kamera ke bidang datar dengan pencahayaan cukup.
                       - Amati model 3D buah Parijoto dan struktur molekul kation flavilium antosianin (C15H11O6+).
                       - Gunakan gestur cubit untuk memperbesar/memperkecil skala molekul.
                    2. Tantangan Quiz Duel Etnosains:
                       - Pilih mode Duel Cepat untuk menguji penguasaan konsep kimia antosianin dan kearifan lokal Muria.
                       - Jawab setiap soal HOTS dalam durasi waktu terbatas guna meraih bonus poin XP dan lencana prestasi.
                    3. Forum Diskusi & Tanya Jawab Kelas:
                       - Sampaikan hipotesis kelompok atau kendala eksperimen praktikum pada ruang diskusi.
                       - Guru dan rekan sejawat dapat memberikan umpan balik, tanggapan saintifik, dan apresiasi solusi.
                """.trimIndent(),
                orderIndex = 4,
                author = "Tim E-Learning Muria",
                lastUpdated = now,
                isDefault = true
            ),

            // ==========================================
            // KATEGORI 2: KURIKULUM (CP, TP & HOTS)
            // ==========================================
            DynamicContent(
                id = "cp_pemahaman_sains",
                category = ContentCategory.CURRICULUM,
                subCategory = "CP_PEMAHAMAN",
                title = "Capaian Pembelajaran (CP) - Elemen Pemahaman Sains (Fase E)",
                content = "Menerapkan prinsip klasifikasi dan strategi pelestarian keanekaragaman hayati Indonesia; menganalisis interaksi antar komponen ekosistem dan pengaruhnya terhadap keseimbangan ekosistem; serta menerapkan konsep biologi dan biokimia antosianin untuk mengatasi permasalahan yang berkaitan dengan lingkungan dan kearifan lokal Gunung Muria.",
                orderIndex = 1,
                author = "Kemendikbudristek & Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "cp_keterampilan_proses",
                category = ContentCategory.CURRICULUM,
                subCategory = "CP_KETERAMPILAN",
                title = "Capaian Pembelajaran (CP) - Elemen Keterampilan Proses (Fase E)",
                content = "Peserta didik mampu menggunakan keterampilan berpikir ilmiah dalam mengidentifikasi masalah eksploitasi hayati, menganalisis informasi fitokimia, serta merumuskan solusi pelestarian terhadap permasalahan dalam kehidupan sehari-hari melalui siklus mengamati, mempertanyakan, merencanakan penyelidikan, memproses data, mengevaluasi, hingga mengomunikasikan hasil.",
                orderIndex = 2,
                author = "Kemendikbudristek & Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "tp_1_keanekaragaman",
                category = ContentCategory.CURRICULUM,
                subCategory = "TUJUAN_PEMBELAJARAN",
                title = "TP 1: Identifikasi Keanekaragaman Hayati Tingkat Spesies",
                content = "Mengidentifikasi berbagai bentuk pemanfaatan tanaman Parijoto (Medinilla speciosa) sebagai representasi keanekaragaman hayati tingkat spesies di kawasan lereng Gunung Muria Kudus.",
                orderIndex = 3,
                author = "Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "tp_2_validasi_ilmiah",
                category = ContentCategory.CURRICULUM,
                subCategory = "TUJUAN_PEMBELAJARAN",
                title = "TP 2: Validasi Ilmiah Kearifan Tradisional Buah Parijoto",
                content = "Menganalisis keterkaitan fungsional antara pemanfaatan tradisional Parijoto (buah ibu hamil & penambah stamina) dengan validasi ilmiah menggunakan data riset biokimia metabolit sekunder (flavonoid, saponin, tanin, dan β-sitosterol).",
                orderIndex = 4,
                author = "Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "tp_3_evaluasi_ekosistem",
                category = ContentCategory.CURRICULUM,
                subCategory = "TUJUAN_PEMBELAJARAN",
                title = "TP 3: Evaluasi Dampak Eksploitasi Terhadap Stabilitas Ekosistem",
                content = "Mengevaluasi dampak lonjakan ekonomi dan eksploitasi liar (over-harvesting) terhadap stabilitas ekosistem, jaring-jaring makanan, serta penurunan populasi alami di zona hutan lindung Muria.",
                orderIndex = 5,
                author = "Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "tp_4_rekayasa_budidaya",
                category = ContentCategory.CURRICULUM,
                subCategory = "TUJUAN_PEMBELAJARAN",
                title = "TP 4: Perancangan Rekayasa Budidaya Ex-Situ Mandiri",
                content = "Merumuskan solusi inovatif dan bertanggung jawab dalam pelestarian keanekaragaman hayati melalui strategi budidaya mandiri (konservasi ex-situ) berbasis rekayasa mikroklimat.",
                orderIndex = 6,
                author = "Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "tp_5_indikasi_geografis",
                category = ContentCategory.CURRICULUM,
                subCategory = "TUJUAN_PEMBELAJARAN",
                title = "TP 5: Analisis Perlindungan Hukum Indikasi Geografis (IG)",
                content = "Mengevaluasi peran sertifikasi Indikasi Geografis (IG) Parijoto Muria dalam menjaga mutu varietas, keaslian genetik, serta mendorong kemandirian ekonomi masyarakat desa penyangga hutan.",
                orderIndex = 7,
                author = "Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "tp_6_komunikasi_kampanye",
                category = ContentCategory.CURRICULUM,
                subCategory = "TUJUAN_PEMBELAJARAN",
                title = "TP 6: Komunikasi Saintifik & Kampanye Digital Pelestarian",
                content = "Mengomunikasikan hasil penyelidikan ilmiah mengenai pelestarian Parijoto dan etnosains Muria dalam bentuk narasi produk kampanye digital yang persuasif dan edukatif.",
                orderIndex = 8,
                author = "Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "rubrik_hots_indikator",
                category = ContentCategory.CURRICULUM,
                subCategory = "INDIKATOR_HOTS",
                title = "Indikator Ketercapaian HOTS & Rubrik Penilaian 6 Aspek",
                content = """
                    Pengukuran Ketercapaian Berpikir Tingkat Tinggi (HOTS):
                    1. Aspek Interpretasi Fenomena Etnosains (C4): Kemampuan menelaah kearifan lokal masyarakat Desa Colo secara kontekstual.
                    2. Aspek Identifikasi Masalah Konservasi (C4): Ketepatan menganalisis akar masalah over-harvesting dan ancaman kepunahan lokal.
                    3. Aspek Formulasi Hipotesis & Variabel (C4): Kemampuan merumuskan hipotesis ilmiah yang dapat diuji mengenai stabilitas ekstrak dan agroklimat.
                    4. Aspek Analisis Fitokimia & Perubahan pH (C5): Keterampilan menginterpretasikan data spektrofotometri dan perubahan warna antosianin pada berbagai rentang pH.
                    5. Aspek Rekayasa Solusi Budidaya Ex-Situ (C6): Kreativitas merancang greenhouse atau pekarangan dengan paranet 60% dan media tanam poros.
                    6. Aspek Sintesis & Komunikasi Kampanye Ilmiah (C6): Kualitas argumen dan orisinalitas pesan kampanye konservasi hayati.
                """.trimIndent(),
                orderIndex = 9,
                author = "Tim Penilai Kurikulum HOTS",
                lastUpdated = now,
                isDefault = true
            ),

            // ==========================================
            // KATEGORI 3: MATERI & PRAKTIKUM ETNOSAINS
            // ==========================================
            DynamicContent(
                id = "modul_parijoto_muria",
                category = ContentCategory.LEARNING_MATERIAL,
                subCategory = "MODUL_ETNOSAINS",
                title = "Modul Etnosains Parijoto Muria: Antara Tradisi, Ekonomi dan Konservasi",
                content = """
                    Indonesia dikenal sebagai negara megabiodiversitas dengan kekayaan spesies tumbuhan pegunungan yang unik. Salah satu spesies penciri ekosistem hutan pegunungan di kawasan Gunung Muria (Kudus, Jepara, dan Pati) adalah Parijoto (Medinilla speciosa). Tanaman semak epifit dari famili Melastomataceae ini menghasilkan buah beri berwarna merah muda hingga ungu kehitaman yang mencolok.

                    Dalam kearifan lokal masyarakat Desa Colo di lereng Gunung Muria, Parijoto memiliki nilai etnosains yang kuat dan diwariskan turun-temurun sejak masa Sunan Muria. Masyarakat mempercayai bahwa konsumsi buah Parijoto oleh ibu hamil dapat menjaga kesehatan janin dan memberikan keturunan yang sehat. Selain dikonsumsi segar atau dibuat rujak, kini Parijoto diolah menjadi sirup, teh herbal, hingga produk olahan UMKM bernilai jual tinggi.

                    Namun, meningkatnya popularitas khasiat Parijoto memicu fenomena eksploitasi tanaman liar (over-harvesting) di hutan lindung Gunung Muria. Tanpa strategi budidaya mandiri (konservasi ex-situ), warisan keanekaragaman hayati ini terancam punah di habitat aslinya.
                """.trimIndent(),
                orderIndex = 1,
                author = "Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "modul_antosianin_asambasa",
                category = ContentCategory.LEARNING_MATERIAL,
                subCategory = "MODUL_ETNOSAINS",
                title = "Modul Biokimia Antosianin & Indikator Asam-Basa Alami",
                content = """
                    Antosianin merupakan pigmen alami larut air dari kelompok flavonoid yang bertanggung jawab atas spektrum warna merah, ungu, hingga biru pada buah dan bunga. Pada buah Parijoto, konsentrasi antosianin meningkat tajam seiring kematangan buah, ditandai perubahan warna dari merah muda pucat menjadi ungu gelap kehitaman.

                    Struktur molekul antosianin sangat peka terhadap perubahan pH lingkungan:
                    • Pada kondisi asam kuat (pH 1–3), antosianin didominasi oleh kation flavilium (merah cerah stabil).
                    • Pada kondisi netral (pH 6–7), molekul bertransformasi menjadi basa kuinonoid (ungu kebiruan).
                    • Pada kondisi basa kuat (pH > 8), terjadi degradasi cincin menjadi kalkon (kuning kehijauan).
                    Karakteristik perubahan warna reversible ini menjadikan ekstrak air buah Parijoto sebagai indikator asam-basa alami ramah lingkungan (green chemistry) yang ideal untuk praktikum sains.
                """.trimIndent(),
                orderIndex = 2,
                author = "Dr. M. Sukron (Ahli Fitokimia Muria)",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "pbl_fase_1_5_lengkap",
                category = ContentCategory.LEARNING_MATERIAL,
                subCategory = "SINTAKS_PBL",
                title = "5 Tahapan Sintaks Problem-Based Learning (PBL) Etnosains",
                content = """
                    Siklus Pembelajaran Berbasis Masalah (PBL) Etnosains Parijoto:
                    • FASE 1: Orientasi Masalah — Mengamati fenomena etnosains Parijoto Gunung Muria serta konflik antara nilai tradisi, lonjakan ekonomi, dan ancaman kelestarian habitat alami.
                    • FASE 2: Organisasi Penyelidikan — Mengintegrasikan konsep kearifan lokal Desa Colo dengan literasi konservasi dan merumuskan hipotesis strategi budidaya.
                    • FASE 3: Penyelidikan Saintifik — Melakukan investigasi laboratorium: morfologi, mikroklimat, ekstraksi antosianin, uji asam-basa, dan status Indikasi Geografis.
                    • FASE 4: Menyajikan Hasil — Merekonstruksi titik temu kognitif sains masyarakat vs sains ilmiah serta merumuskan produk kampanye konservasi.
                    • FASE 5: Evaluasi & Refleksi — Menganalisis dan mengevaluasi seluruh proses pemecahan masalah serta menguji kemampuan berpikir kritis HOTS.
                """.trimIndent(),
                orderIndex = 3,
                author = "Ahzami, S.Pd., Gr",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "petunjuk_praktikum_akt1",
                category = ContentCategory.LEARNING_MATERIAL,
                subCategory = "PETUNJUK_PRAKTIKUM",
                title = "Petunjuk Kerja Praktikum Aktivitas 1: Eksplorasi Fenomena Etnosains",
                content = """
                    Tujuan: Mengidentifikasi fenomena eksploitasi Parijoto dan merumuskan masalah konservasi.
                    Langkah Kerja:
                    1. Amati spesimen buah dan daun Parijoto segar atau gambar makroskopis resolusi tinggi.
                    2. Catat karakter morfologi: pertulangan daun melengkung sejajar (3-5 vena) dan warna buah buni.
                    3. Pelajari data wawancara masyarakat lokal Desa Colo mengenai khasiat kesehatan dan tradisi buah ibu hamil.
                    4. Rumuskan minimal 2 pertanyaan penyelidikan mengenai kaitan antara kandungan kimia dengan manfaat tradisional.
                    5. Susun hipotesis awal mengenai solusi budidaya mandiri pekarangan (ex-situ) untuk mencegah kepunahan hutan lindung.
                """.trimIndent(),
                orderIndex = 4,
                author = "Tim Laboratorium Etnosains",
                lastUpdated = now,
                isDefault = true
            ),
            DynamicContent(
                id = "petunjuk_praktikum_akt2",
                category = ContentCategory.LEARNING_MATERIAL,
                subCategory = "PETUNJUK_PRAKTIKUM",
                title = "Petunjuk Kerja Praktikum Aktivitas 2: Ekstraksi Antosianin & Uji Indikator Alami",
                content = """
                    Tujuan: Mengekstraksi antosianin Parijoto dan menguji perubahan warna pada berbagai larutan pH asam-basa.
                    Alat dan Bahan:
                    - Buah Parijoto ungu matang (10-15 gram)
                    - Mortar dan alu, labu erlenmeyer, pipet tetes, tabung reaksi (minimal 3 buah), rak tabung
                    - Akuades hangat (suhu 45-50°C), kertas saring atau saringan halus
                    - Larutan uji: Air cuka / HCl 0.1 M (asam), Air mineral / akuades (netral), Air kapur sirih / NaOH 0.1 M (basa)
                    
                    Prosedur Kerja:
                    1. Haluskan buah Parijoto matang pada mortar, tambahkan 20 mL akuades hangat, gerus perlahan selama 3 menit.
                    2. Saring ekstrak menggunakan kertas saring hingga diperoleh filtrat berwarna ungu kemerahan jernih.
                    3. Beri label 3 tabung reaksi: Tabung A (Asam), Tabung B (Netral), Tabung C (Basa).
                    4. Tuangkan masing-masing 2 mL ekstrak filtrat ke dalam Tabung A, B, dan C.
                    5. Teteskan 5 tetes cuka/HCl ke Tabung A, goyangkan perlahan, amati perubahan warna menjadi merah cerah.
                    6. Teteskan 5 tetes akuades ke Tabung B sebagai kontrol pembanding (warna ungu asli).
                    7. Teteskan 5 tetes air kapur/NaOH ke Tabung C, amati perubahan warna menjadi hijau tua/kekuningan.
                    8. Ambil foto hasil tabung reaksi melalui kamera smartphone dan unggah pada lembar kerja digital aplikasi.
                    9. Lengkapi tabel rekonstruksi sains dan tarik kesimpulan berdasarkan teori ionisasi pigmen antosianin!
                """.trimIndent(),
                orderIndex = 5,
                author = "Tim Laboratorium Etnosains",
                lastUpdated = now,
                isDefault = true
            )
        )
    }
}
