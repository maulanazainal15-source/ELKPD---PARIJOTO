package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val generationConfig: GenerationConfig? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null
)

@Serializable
data class GenerationConfig(
    val temperature: Float? = 0.4f,
    val topP: Float? = 0.9f
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate> = emptyList()
)

@Serializable
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.1-pro-preview:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiAiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiApiService::class.java)
    }

    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    private const val PEDAGOGICAL_SYSTEM_INSTRUCTION = """
        Anda adalah AI Tutor Biologi & Etnosains untuk E-LKPD Keanekaragaman Hayati: Eksplorasi Etnosains Parijoto (Medinilla speciosa) SMA/MA Kelas X Fase E.
        ATURAN PEDAGOGIS KETAT:
        1. JANGAN PERNAH memberikan jawaban langsung yang siap disalin untuk soal tugas atau evaluasi.
        2. Berikan respons berupa: (a) Pertanyaan Pemantik (Scaffolding), (b) Penjelasan Konsep Biologi/Etnosains (seperti epifit, flavonoid, saponin, tanin, beta-sitosterol, konservasi in-situ/ex-situ, Indikasi Geografis), (c) Koreksi miskonsepsi jika ada, dan (d) Saran pengembangan argumen ilmiah.
        3. Jangan pernah mengarang nama jurnal atau referensi ilmiah fiktif.
        4. Gunakan Bahasa Indonesia yang ramah, akademis, dan memotivasi siswa SMA.
    """

    suspend fun askAiTutor(studentQuestionOrTopic: String, contextPhase: String): String =
        withContext(Dispatchers.IO) {
            if (!isApiKeyConfigured()) {
                return@withContext provideOfflinePedagogicalScaffolding(studentQuestionOrTopic, contextPhase)
            }
            try {
                val prompt = "Konteks Pembelajaran: $contextPhase.\nPertanyaan/Diskusi Siswa: $studentQuestionOrTopic\n" +
                        "Berikan bimbingan belajar (pertanyaan pemantik, konsep kunci, dan petunjuk) tanpa memberikan kunci jawaban langsung."
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    systemInstruction = Content(parts = listOf(Part(text = PEDAGOGICAL_SYSTEM_INSTRUCTION))),
                    generationConfig = GenerationConfig(temperature = 0.4f)
                )
                val response = api.generateContent(BuildConfig.GEMINI_API_KEY, request)
                response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: provideOfflinePedagogicalScaffolding(studentQuestionOrTopic, contextPhase)
            } catch (e: Exception) {
                provideOfflinePedagogicalScaffolding(studentQuestionOrTopic, contextPhase) +
                        "\n\n(Catatan: Terhubung ke Panduan Pedagogis Lokal karena kendala jaringan/API: ${e.localizedMessage ?: "Offline"})"
            }
        }

    suspend fun evaluateAnswerWithRubric(
        questionText: String,
        studentAnswer: String,
        rubricFocus: String
    ): Pair<Int, String> = withContext(Dispatchers.IO) {
        if (studentAnswer.isBlank()) {
            return@withContext 0 to "Jawaban masih kosong. Silakan lengkapi analisis Anda terlebih dahulu."
        }
        if (!isApiKeyConfigured()) {
            return@withContext evaluateLocallyWithRubricHeuristics(questionText, studentAnswer, rubricFocus)
        }
        try {
            val prompt = """
                Evaluasi jawaban siswa SMA Kelas X berikut berdasarkan 6 aspek rubrik E-LKPD Etnosains Parijoto (Skala 1-4 per aspek, konversi ke 0-100):
                1. Ketepatan konsep biologi
                2. Penggunaan data (flavonoid, saponin, tanin, beta-sitosterol, mikroklimat)
                3. Kemampuan analisis
                4. Berpikir kritis
                5. Solusi konservasi (in-situ / ex-situ / Indikasi Geografis)
                6. Kemampuan komunikasi
                
                Fokus Soal: $rubricFocus
                Pertanyaan: $questionText
                Jawaban Siswa: "$studentAnswer"
                
                ATURAN:
                - Jangan mengarang referensi fiktif.
                - Berikan baris pertama dalam format: SKOR_ESTIMASI: [angka 0-100]
                - Diikuti ulasan kelebihan jawaban, koreksi miskonsepsi, dan saran perbaikan.
                - Akhiri dengan kalimat: "Penilaian AI bersifat bantuan dan dapat ditinjau kembali oleh guru."
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                systemInstruction = Content(parts = listOf(Part(text = PEDAGOGICAL_SYSTEM_INSTRUCTION))),
                generationConfig = GenerationConfig(temperature = 0.2f)
            )
            val response = api.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (text != null) {
                val regex = Regex("SKOR_ESTIMASI:\\s*(\\d+)")
                val match = regex.find(text)
                val score = match?.groupValues?.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 100) ?: 78
                score to text
            } else {
                evaluateLocallyWithRubricHeuristics(questionText, studentAnswer, rubricFocus)
            }
        } catch (e: Exception) {
            evaluateLocallyWithRubricHeuristics(questionText, studentAnswer, rubricFocus)
        }
    }

    private fun provideOfflinePedagogicalScaffolding(query: String, phase: String): String {
        val q = query.lowercase()
        return buildString {
            appendLine("💡 **Bimbingan Pedagogis E-LKPD ($phase)**")
            appendLine()
            when {
                q.contains("ekonomi") || q.contains("populasi") || q.contains("punah") -> {
                    appendLine("• **Pertanyaan Pemantik:** Ketika permintaan pasar wisata di Desa Colo meningkat pesat sementara pasokan hanya mengandalkan panen liar di hutan, apa yang terjadi pada laju regenerasi alami tumbuhan *Medinilla speciosa*?")
                    appendLine("• **Konsep Kunci:** Tinjau konsep *over-harvesting* (eksploitasi berlebih) dan interaksi trofik antara tumbuhan penghasil buah buni dengan fauna frugivora (burung pemakan buah) di ekosistem Gunung Muria.")
                    appendLine("• **Saran Pengembangan:** Hubungkan dampak pencabutan tanaman induk dengan hilangnya penyebaran biji alami di lantai hutan.")
                }
                q.contains("senyawa") || q.contains("flavonoid") || q.contains("antioksidan") || q.contains("hamil") -> {
                    appendLine("• **Pertanyaan Pemantik:** Mengapa buah Parijoto berwarna merah muda hingga ungu pekat? Senyawa metabolit sekunder apa yang memberi pigmen tersebut sekaligus menangkal radikal bebas?")
                    appendLine("• **Konsep Kunci:** Perhatikan fungsi **Flavonoid (Antosianin)** sebagai antioksidan pendonor elektron untuk menstabilkan radikal bebas (ROS), **Saponin** sebagai imunomodulator/antibakteri, **Tanin** sebagai pelindung jaringan, serta **β-sitosterol** dalam mendukung regulasi fisiologis.")
                    appendLine("• **Koreksi Miskonsepsi:** Ingat bahwa khasiat kesehatan berasal dari perlindungan seluler dan metabolisme biokimia yang terukur, bukan sekadar mitos tanpa dasar fisiologis.")
                }
                q.contains("ig") || q.contains("indikasi geografis") || q.contains("konservasi") || q.contains("budidaya") -> {
                    appendLine("• **Pertanyaan Pemantik:** Bagaimana caranya agar petani tetap mendapat keuntungan ekonomi tinggi tanpa harus memetik Parijoto di dalam hutan lindung?")
                    appendLine("• **Konsep Kunci:** Bandingkan **Konservasi In-Situ** (di habitat asli hutan Gunung Muria) dengan **Konservasi Ex-Situ** (budidaya mandiri di kebun/pekarangan menggunakan paranet 50-70% dan media humus lembap). Status **Indikasi Geografis (IG)** memberikan perlindungan hukum atas keaslian geografis sekaligus mewajibkan SOP budidaya ramah lingkungan.")
                }
                else -> {
                    appendLine("• **Pertanyaan Pemantik:** Bagaimana kearifan lokal masyarakat lereng Gunung Muria dapat berjalan beriringan dengan bukti sains biologi modern?")
                    appendLine("• **Konsep Kunci:** Fokuskan analisis Anda pada tiga pilar utama E-LKPD ini: (1) Keanekaragaman Hayati Tingkat Spesies (*Medinilla speciosa*), (2) Validasi Biokimia Metabolit Sekunder, dan (3) Strategi Konservasi Berkelanjutan.")
                    appendLine("• **Petunjuk:** Gunakan data dari wacana stimulus dan tabel investigasi saintifik untuk memperkuat argumen ilmiahmu.")
                }
            }
        }
    }

    private fun evaluateLocallyWithRubricHeuristics(
        questionText: String,
        studentAnswer: String,
        rubricFocus: String
    ): Pair<Int, String> {
        val words = studentAnswer.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val lower = studentAnswer.lowercase()

        val biologyKeywords = listOf(
            "medinilla", "spesies", "ekosistem", "habitat", "populasi", "epifit",
            "flavonoid", "saponin", "tanin", "sitosterol", "antioksidan", "radikal bebas",
            "konservasi", "budidaya", "ex-situ", "in-situ", "indikasi geografis", "muria",
            "kelembapan", "naungan", "etnosains", "frugivora", "rantai makanan"
        )
        val matchedKeywords = biologyKeywords.filter { lower.contains(it) }

        val lengthPoints = (words.size * 1.5).toInt().coerceAtMost(45)
        val keywordPoints = (matchedKeywords.size * 8).coerceAtMost(45)
        val structurePoints = if (studentAnswer.contains(".") && words.size >= 15) 10 else 4
        val totalScore = (lengthPoints + keywordPoints + structurePoints).coerceIn(35, 95)

        val feedback = buildString {
            appendLine("SKOR_ESTIMASI: $totalScore (Skala 0-100 | Setara Skala Rubrik ${(totalScore / 25f).coerceIn(1f, 4f)})")
            appendLine("• Fokus Rubrik: $rubricFocus")
            appendLine("• Panjang Argumen: ${words.size} kata.")
            if (matchedKeywords.isNotEmpty()) {
                appendLine("• Konsep Ilmiah Terdeteksi: ${matchedKeywords.joinToString(", ")}.")
            } else {
                appendLine("• Saran Konsep: Tambahkan istilah kunci biologi seperti *Medinilla speciosa*, metabolit sekunder (flavonoid/saponin/tanin/β-sitosterol), atau strategi konservasi ex-situ/in-situ.")
            }
            if (words.size < 20) {
                appendLine("• Saran Pengembangan: Uraikan hubungan sebab-akibat secara lebih mendalam menggunakan data dari hasil pengamatan dan studi literatur.")
            } else {
                appendLine("• Apresiasi: Struktur argumen sudah cukup baik dalam mengaitkan fenomena etnosains dengan konsep biologi.")
            }
            append("Penilaian AI bersifat bantuan dan dapat ditinjau kembali oleh guru.")
        }
        return totalScore to feedback
    }
}
