package com.example.domain

import com.example.model.BiologyDimensionRow
import com.example.model.EssayQuestion
import com.example.model.McqQuestion
import com.example.model.PblPhaseInfo

/**
 * KONTEN KURIKULER E-LKPD KEANEKARAGAMAN HAYATI: EKSPLORASI ETNOSAINS PARIJOTO (Medinilla speciosa)
 * Penulis: Ahzami, S.Pd., Gr | Tahun: 2026 | Kelas X / Fase E (2 JP / 2 x 45 Menit)
 */
object ElpkdCurriculumData {

    val pblPhases = listOf(
        PblPhaseInfo(
            phaseNumber = 1,
            shortTitle = "Orientasi Masalah",
            fullTitle = "FASE 1: Orientasi Peserta Didik pada Masalah",
            description = "Mengamati fenomena etnosains Parijoto Gunung Muria serta konflik antara nilai tradisi, lonjakan ekonomi, dan ancaman kelestarian habitat alami."
        ),
        PblPhaseInfo(
            phaseNumber = 2,
            shortTitle = "Organisasi Penyelidikan",
            fullTitle = "FASE 2: Pengorganisasian Penyelidikan",
            description = "Mengintegrasikan konsep kearifan lokal masyarakat Desa Colo dengan literasi konservasi dan merumuskan hipotesis strategi budidaya."
        ),
        PblPhaseInfo(
            phaseNumber = 3,
            shortTitle = "Penyelidikan Saintifik",
            fullTitle = "FASE 3: Membimbing Penyelidikan Individu/Kelompok",
            description = "Melakukan investigasi saintifik morfologi, mikroklimat, metabolit sekunder (flavonoid, saponin, tanin, β-sitosterol), dan status Indikasi Geografis."
        ),
        PblPhaseInfo(
            phaseNumber = 4,
            shortTitle = "Menyajikan Hasil",
            fullTitle = "FASE 4: Mengembangkan dan Menyajikan Hasil",
            description = "Merekonstruksi titik temu kognitif antara pengetahuan lokal dan validasi ilmiah serta merumuskan solusi konservasi berbasis budidaya mandiri."
        ),
        PblPhaseInfo(
            phaseNumber = 5,
            shortTitle = "Evaluasi & Refleksi",
            fullTitle = "FASE 5: Menganalisis dan Mengevaluasi Proses Pemecahan Masalah",
            description = "Menyimpulkan integrasi Etnosains-Biologi dan menguji kemampuan berpikir tingkat tinggi (HOTS) melalui evaluasi komprehensif."
        )
    )

    const val CAPAIAN_PEMBELAJARAN_PEMAHAMAN =
        "Menerapkan prinsip klasifikasi dan strategi pelestarian keanekaragaman hayati Indonesia; menganalisis interaksi antar komponen ekosistem dan pengaruhnya terhadap keseimbangan ekosistem; serta menerapkan konsep biologi untuk mengatasi permasalahan yang berkaitan dengan lingkungan."

    const val CAPAIAN_PEMBELAJARAN_KETERAMPILAN =
        "Peserta didik mampu menggunakan keterampilan berpikir ilmiah dalam mengidentifikasi masalah, menganalisis informasi, serta merumuskan solusi terhadap permasalahan dalam kehidupan sehari-hari melalui siklus mengamati, mempertanyakan, merencanakan penyelidikan, memproses data, mengevaluasi, hingga mengomunikasikan hasil."

    val tujuanPembelajaran = listOf(
        "Mengidentifikasi berbagai bentuk pemanfaatan tanaman Parijoto sebagai representasi keanekaragaman hayati tingkat spesies.",
        "Menganalisis keterkaitan fungsional antara pemanfaatan tradisional Parijoto dengan validasi ilmiah menggunakan data riset biokimia.",
        "Mengevaluasi dampak eksploitasi tanaman lokal terhadap stabilitas ekosistem dan penurunan populasi di habitat asli Gunung Muria.",
        "Memproses data hasil studi literatur mengenai status konservasi Parijoto.",
        "Merumuskan solusi inovatif dan bertanggung jawab dalam pelestarian keanekaragaman hayati melalui strategi budidaya mandiri.",
        "Mengomunikasikan hasil penyelidikan ilmiah mengenai pelestarian Parijoto dalam bentuk produk kampanye digital."
    )

    val petunjukPenggunaan = listOf(
        "Membaca wacana stimulus dan informasi saintifik dengan seksama.",
        "Menggunakan hyperlink sumber belajar yang tersedia untuk memperkaya literasi.",
        "Mengakses QR Code atau video dokumenter pembelajaran untuk mengamati konteks lapangan di Gunung Muria.",
        "Berdiskusi secara kolaboratif bersama kelompok (atau berefleksi secara mandiri pada mode individu).",
        "Mengisi seluruh jawaban melalui kolom input digital yang dilengkapi fitur penyimpanan otomatis (autosave).",
        "Mengunggah gambar morfologi tanaman Parijoto melalui kamera atau galeri beserta deskripsi pengamatan.",
        "Menyelesaikan seluruh tahapan mulai dari Pendahuluan, Aktivitas 1, Aktivitas 2, hingga Uji Kemampuan.",
        "Menekan tombol Simpan / Submit dan mengunduh laporan PDF Hasil Pembelajaran."
    )

    const val STIMULUS_AKTIVITAS_1_JUDUL = "Parijoto Muria: Antara Tradisi, Ekonomi dan Konservasi"

    val stimulusAktivitas1Paragraphs = listOf(
        "Indonesia dikenal sebagai negara megabiodiversitas dengan kekayaan spesies tumbuhan pegunungan yang unik. Salah satu spesies penciri ekosistem hutan pegunungan di kawasan Gunung Muria (Kudus, Jepara, dan Pati) adalah Parijoto (Medinilla speciosa). Tanaman semak epifit dari famili Melastomataceae ini menghasilkan buah beri berwarna merah muda hingga ungu kehitaman yang mencolok.",
        "Dalam kearifan lokal masyarakat Desa Colo di lereng Gunung Muria, Parijoto memiliki nilai etnosains yang kuat dan diwariskan turun-temurun sejak masa Sunan Muria. Masyarakat mempercayai bahwa konsumsi buah Parijoto oleh ibu hamil dapat menjaga kesehatan janin dan memberikan keturunan yang sehat. Selain dikonsumsi segar atau dibuat rujak, kini Parijoto diolah menjadi sirup, teh herbal, hingga produk olahan UMKM bernilai jual tinggi.",
        "Namun, meningkatnya popularitas khasiat Parijoto dan lonjakan permintaan pasar wisatawan memicu fenomena eksploitasi tanaman liar (over-harvesting) di hutan lindung Gunung Muria. Sebagian pencari buah memetik ranting secara masif bahkan mencabut tanaman induk dari pohon inang maupun tebing hutan untuk dijual sebagai tanaman hias.",
        "Akibatnya, kerusakan habitat mikro dan penurunan populasi alami Parijoto di zona hutan Gunung Muria kian mengkhawatirkan. Padahal, buah Parijoto merupakan sumber pakan alami bagi berbagai spesies burung pemakan buah (frugivora) dan serangga penyerbuk di ekosistem Muria. Tanpa strategi transisi menuju budidaya mandiri (konservasi ex-situ) yang terstandar di pekarangan warga, warisan keanekaragaman hayati dan tradisi lokal ini terancam mengalami kepunahan lokal."
    )

    const val MATERI_AKTIVITAS_2_RINGKASAN =
        "Parijoto (Medinilla speciosa) merupakan tumbuhan semak epifit evergreen dari famili Melastomataceae yang tumbuh optimal di tempat teduh dengan kelembapan tinggi."

    val kartuDataSaintifikAktivitas2 = listOf(
        Triple(
            "Keanekaragaman Tingkat Spesies & Morfologi",
            "Taksonomi: Famili Melastomataceae, Genus Medinilla, Spesies Medinilla speciosa.",
            "Memiliki daun tunggal berhadapan, bentuk lonjong dengan ujung meruncing dan 3–5 tulang daun melengkung sejajar yang khas. Bunga majemuk dalam malai, buah buni bulat berdiameter 5–8 mm berwarna merah muda saat muda dan ungu gelap saat matang."
        ),
        Triple(
            "Habitat & Mikroklimat Gunung Muria",
            "Elevasi 800–1.400 mdpl dengan naungan kanopi rapat.",
            "Tumbuh optimal pada suhu udara sejuk (18°C–24°C), kelembapan udara tinggi (75%–90%), intensitas cahaya teduh (naungan 50%–70%), serta media tumbuh kaya humus organik yang poros namun lembap."
        ),
        Triple(
            "Kandungan Biokimia (Metabolit Sekunder)",
            "Validasi laboratorium fitokimia ekstrak buah dan daun Parijoto.",
            "• Flavonoid & Antosianin: Antioksidan kuat penangkal radikal bebas dan pemberi pigmen ungu-merah.\n• Saponin: Berperan sebagai agen antimikroba, imunomodulator, dan penurun kolesterol.\n• Tanin: Senyawa polifenol astringen pelindung jaringan dan antibakteri.\n• β-sitosterol (Beta-sitosterol): Fitosterol yang mendukung keseimbangan hormonal, kesuburan, dan antiinflamasi."
        ),
        Triple(
            "Ancaman Over-Harvesting & Status Konservasi",
            "Eksploitasi liar vs Pelestarian In-Situ & Ex-Situ serta Indikasi Geografis (IG).",
            "Pengambilan bibit dan buah secara langsung dari hutan lindung menurunkan regenerasi alami. Diperlukan perlindungan Indikasi Geografis (IG) Parijoto Gunung Muria serta budidaya mandiri (ex-situ) menggunakan paranet dan media humus bambu/pakis agar tekanan terhadap populasi hutan berkurang."
        )
    )

    val tabelDimensiBiologi = listOf(
        BiologyDimensionRow(
            id = "row_1",
            dimensionName = "Kandungan & Khasiat Buah",
            hintLokal = "Warisan tradisi ibu hamil & kesehatan keturunan",
            hintIlmiah = "Kaya Flavonoid, Saponin, Tanin, β-sitosterol",
            hintManfaat = "Antioksidan, antibakteri, nutrisi sel & hormonal",
            hintBudidaya = "Panen saat buah ungu tua agar antosianin optimal",
            hintIndikator = "Warna kulit buah berubah dari merah muda ke ungu gelap"
        ),
        BiologyDimensionRow(
            id = "row_2",
            dimensionName = "Ekologi & Syarat Tumbuh",
            hintLokal = "Tumbuh subur di lereng sejuk & rimbun Desa Colo",
            hintIlmiah = "Epifit/semak bawah kanopi, lembap (75-90%), 800-1400 mdpl",
            hintManfaat = "Menjaga interaksi burung frugivora & mikrohabitat hutan",
            hintBudidaya = "Gunakan naungan paranet 60% & media humus poros",
            hintIndikator = "Daun segar mengilap, tidak terbakar sinar matahari langsung"
        ),
        BiologyDimensionRow(
            id = "row_3",
            dimensionName = "Pelestarian & Pemanfaatan",
            hintLokal = "Pantangan merusak pohon induk di kawasan keramat/hutan",
            hintIlmiah = "Konservasi in-situ hutan lindung & budidaya ex-situ stek/biji",
            hintManfaat = "Keberlanjutan ekonomi UMKM tanpa merusak populasi liar",
            hintBudidaya = "Perbanyakan vegetatif (stek batang) & generatif di greenhouse",
            hintIndikator = "Populasi hutan tetap stabil meski produksi UMKM meningkat"
        )
    )

    val defaultMcqQuestions = listOf(
        McqQuestion(
            id = "mcq_1",
            number = 1,
            pblPhase = 1,
            indicator = "Mengevaluasi dampak eksploitasi Parijoto terhadap jaring-jaring makanan ekosistem Gunung Muria (HOTS - C5)",
            stimulus = "Lonjakan permintaan pasar terhadap sirup dan buah segar Parijoto (Medinilla speciosa) di kawasan wisata Gunung Muria mendorong sebagian oknum memanen buah secara habis-habisan dan mencabut tanaman induk dari hutan lindung.",
            question = "Apabila fenomena over-harvesting tersebut berlangsung terus-menerus hingga memicu kepunahan lokal Parijoto di hutan Gunung Muria, dampak ekologis berantai yang paling tepat terjadi pada ekosistem tersebut adalah...",
            options = mapOf(
                "A" to "Meningkatnya populasi burung frugivora karena berkurangnya semak belukar yang menghalangi ruang terbang di bawah kanopi.",
                "B" to "Terganggunya keseimbangan jaring-jaring makanan akibat hilangnya sumber pakan burung pemakan buah dan menurunnya dispersi biji tumbuhan hutan.",
                "C" to "Pohon inang tempat Parijoto menempel akan mati secara serentak karena kehilangan suplai unsur hara dari akar Parijoto.",
                "D" to "Kelembapan mikro di lantai hutan meningkat tajam karena berkurangnya transpirasi dari daun Parijoto.",
                "E" to "Terjadinya mutasi genetik spontan pada tumbuhan famili Melastomataceae lain untuk menggantikan peran Parijoto dalam waktu singkat."
            ),
            correctOption = "B",
            explanation = "Parijoto menghasilkan buah buni yang menjadi pakan penting bagi burung frugivora dan fauna hutan Muria. Kepunahan lokal akan memutus rantai makanan dan menghambat mutualisme penyebaran biji."
        ),
        McqQuestion(
            id = "mcq_2",
            number = 2,
            pblPhase = 3,
            indicator = "Menganalisis keterkaitan tradisi etnosains masyarakat Desa Colo dengan bukti biokimia metabolit sekunder (HOTS - C4)",
            stimulus = "Masyarakat lereng Gunung Muria secara turun-temurun memanfaatkan buah Parijoto untuk menjaga kesehatan ibu hamil. Riset fitokimia modern membuktikan bahwa buah Parijoto mengandung flavonoid (antosianin), saponin, tanin, dan β-sitosterol.",
            question = "Manakah analisis yang paling tepat dalam menjelaskan titik temu kognitif (validasi ilmiah) antara kepercayaan tradisional tersebut dengan fungsi fisiologis senyawa biokimia Parijoto?",
            options = mapOf(
                "A" to "Kandungan tanin berfungsi mengubah struktur DNA janin secara langsung sesuai harapan orang tua.",
                "B" to "Flavonoid dan β-sitosterol bekerja sebagai antioksidan penangkal stres oksidatif seluler serta mendukung regulasi fisiologis dan imunitas tubuh ibu hamil.",
                "C" to "Saponin menghasilkan kalori tinggi yang dapat menggantikan seluruh kebutuhan karbohidrat dan protein selama masa kehamilan.",
                "D" to "Pigmen ungu antosianin hanya berfungsi sebagai pewarna alami tanpa memiliki aktivitas biologis di dalam metabolisme tubuh manusia.",
                "E" to "Senyawa metabolit sekunder Parijoto hanya aktif apabila tanaman diambil langsung dari hutan liar dan hilang total jika dibudidayakan."
            ),
            correctOption = "B",
            explanation = "Flavonoid (termasuk antosianin) merupakan antioksidan kuat yang melindungi sel dari radikal bebas, sedangkan β-sitosterol dan saponin mendukung keseimbangan fisiologis dan imunitas."
        ),
        McqQuestion(
            id = "mcq_3",
            number = 3,
            pblPhase = 3,
            indicator = "Merancang rekayasa mikroklimat budidaya ex-situ berdasarkan karakter ekologi Medinilla speciosa (HOTS - C6)",
            stimulus = "Seorang kelompok tani di Desa Colo ingin menghentikan pengambilan Parijoto dari hutan lindung dengan membangun kebun budidaya mandiri (ex-situ) di lahan pekarangan terbuka pada ketinggian 650 mdpl yang bersuhu lebih hangat dan terik.",
            question = "Berdasarkan karakteristik fisiologis Medinilla speciosa sebagai tumbuhan semak epifit bawah kanopi, modifikasi agroklimat manakah yang paling tepat diterapkan agar budidaya berhasil?",
            options = mapOf(
                "A" to "Menanam Parijoto di lahan terbuka penuh cahaya matahari 100% dengan media tanah lempung padat agar air tergenang.",
                "B" to "Memasang naungan paranet (50%–70%), menggunakan media tanam poros kaya humus (pakis/sekam/kompos daun bambu), dan menjaga kelembapan udara melalui pengabutan.",
                "C" to "Memberikan pupuk kimia dosis tinggi setiap hari tanpa naungan agar daun cepat menebal.",
                "D" to "Menyimpan tanaman di dalam ruangan tertutup rapat tanpa ventilasi dan tanpa cahaya matahari sama sekali.",
                "E" to "Memangkas seluruh daun tua dan menyiram tanaman menggunakan air garam untuk merangsang pembungaan."
            ),
            correctOption = "B",
            explanation = "Medinilla speciosa memerlukan habitat teduh (naungan kanopi), kelembapan tinggi, dan media kaya bahan organik dengan aerasi/drainase baik."
        ),
        McqQuestion(
            id = "mcq_4",
            number = 4,
            pblPhase = 4,
            indicator = "Mengevaluasi peran status Indikasi Geografis (IG) terhadap konservasi dan ekonomi berkelanjutan (HOTS - C5)",
            stimulus = "Parijoto Gunung Muria didorong untuk memperoleh perlindungan hukum berupa sertifikasi Indikasi Geografis (IG) yang menetapkan standar budidaya, keaslian varietas lokal, serta batas wilayah produksi.",
            question = "Mengapa pemberian status Indikasi Geografis (IG) memiliki peran strategis dalam mengintegrasikan pelestarian keanekaragaman hayati dengan kesejahteraan masyarakat lokal?",
            options = mapOf(
                "A" to "Karena status IG mewajibkan seluruh masyarakat mengambil buah hanya dari dalam hutan lindung agar keasliannya terjaga.",
                "B" to "Karena status IG melarang seluruh warga Desa Colo memperjualbelikan buah Parijoto dalam bentuk apa pun.",
                "C" to "Karena IG melindungi keunikan genetik spesifik lokasi, memberikan nilai tambah ekonomi bagi petani budidaya resmi, dan mewajibkan standar panen ramah konservasi.",
                "D" to "Karena IG bertujuan mengganti tanaman Parijoto lokal dengan tanaman impor yang lebih cepat berbuah.",
                "E" to "Karena sertifikasi IG hanya bermanfaat bagi peneliti asing dan tidak berkaitan dengan petani lokal."
            ),
            correctOption = "C",
            explanation = "Indikasi Geografis (IG) melindungi reputasi dan kekhasan hayati lokal sekaligus mengatur tata kelola produksi berbasis budidaya berkelanjutan sehingga nilai ekonomi meningkat tanpa merusak hutan."
        ),
        McqQuestion(
            id = "mcq_5",
            number = 5,
            pblPhase = 5,
            indicator = "Menganalisis tingkatan keanekaragaman hayati dan strategi pelestarian berkelanjutan (HOTS - C4)",
            stimulus = "Di kawasan ekosistem Gunung Muria terdapat berbagai tumbuhan famili Melastomataceae, seperti Parijoto (Medinilla speciosa), Senggani (Melastoma malabathricum), dan Harendong bulu (Clidemia hirta) yang memiliki ciri morfologi daun melengkung serupa namun berbeda spesies.",
            question = "Keberadaan berbagai tumbuhan berbeda spesies dalam satu famili yang sama di ekosistem Gunung Muria tersebut merepresentasikan keanekaragaman hayati tingkat..., dan langkah paling bertanggung jawab untuk menjaganya adalah...",
            options = mapOf(
                "A" to "Tingkat Gen; melakukan hibridisasi paksa seluruh spesies menjadi satu tanaman tunggal.",
                "B" to "Tingkat Spesies (Jenis); memadukan perlindungan habitat asli di hutan lindung (in-situ) dengan pengembangan kebun bibit warga (ex-situ).",
                "C" to "Tingkat Ekosistem; menebang spesies Senggani dan Harendong agar hanya Parijoto yang hidup di Gunung Muria.",
                "D" to "Tingkat Gen; memindahkan seluruh tanaman dari hutan Gunung Muria ke luar negeri.",
                "E" to "Tingkat Spesies (Jenis); membakar semak belukar bawah kanopi agar nutrisi tanah meningkat."
            ),
            correctOption = "B",
            explanation = "Variasi antar spesies (Medinilla speciosa, Melastoma malabathricum, Clidemia hirta) dalam satu famili merupakan keanekaragaman hayati tingkat spesies, yang dilestarikan melalui kombinasi konservasi in-situ dan ex-situ."
        )
    )

    val defaultEssayQuestions = listOf(
        EssayQuestion(
            id = "essay_1",
            number = 1,
            categoryTitle = "1. Interpretasi Fenomena Ekologis",
            question = "Jelaskan secara komprehensif bagaimana lonjakan permintaan ekonomi buah Parijoto yang tidak diimbangi literasi konservasi dapat mengganggu keseimbangan rantai makanan dan mikrohabitat di ekosistem hutan Gunung Muria!",
            rubricFocus = "Aspek Ketepatan Konsep Biologi & Kemampuan Analisis Ekosistem"
        ),
        EssayQuestion(
            id = "essay_2",
            number = 2,
            categoryTitle = "2. Evaluasi Status Konservasi & Validasi Ilmiah",
            question = "Bagaimana data riset biokimia (kandungan flavonoid, saponin, tanin, dan β-sitosterol) serta perlindungan status Indikasi Geografis (IG) dapat menjadi landasan ilmiah untuk mengedukasi masyarakat agar menghentikan eksploitasi bibit liar?",
            rubricFocus = "Aspek Penggunaan Data Saintifik & Berpikir Kritis"
        ),
        EssayQuestion(
            id = "essay_3",
            number = 3,
            categoryTitle = "3. Sintesis Solusi Pelestarian",
            question = "Rancanglah sebuah strategi budidaya mandiri (konservasi ex-situ) Parijoto di lingkungan rumah/sekolah dengan memperhatikan syarat mikroklimat (suhu, kelembapan, intensitas cahaya, dan media tanam) agar produksi berkelanjutan tanpa merusak hutan lindung!",
            rubricFocus = "Aspek Solusi Konservasi & Ketepatan Konsep Agroklimat"
        ),
        EssayQuestion(
            id = "essay_4",
            number = 4,
            categoryTitle = "4. Komunikasi Digital & Literasi Etnosains",
            question = "Tuliskan sebuah narasi kampanye digital singkat (slogan, pesan utama ilmiah-budaya, dan ajakan bertindak) yang ditujukan kepada generasi muda dan wisatawan Gunung Muria untuk melestarikan Parijoto!",
            rubricFocus = "Aspek Kemampuan Komunikasi Ilmiah & Integrasi Etnosains"
        )
    )
}
