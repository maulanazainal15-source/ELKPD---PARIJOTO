package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * 1. USER ENTITY
 * Menyimpan identitas peserta didik, mode kerja (INDIVIDU/KELOMPOK), dan role (SISWA/GURU).
 */
@Serializable
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val nis: String,
    val kelas: String,
    val kelompok: String,
    val modeKerja: String = "KELOMPOK", // "INDIVIDU" atau "KELOMPOK"
    val role: String = "SISWA",         // "SISWA" atau "GURU"
    val isActive: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 2. PROGRESS ENTITY
 * Menyimpan status penyelesaian tiap sub-aktivitas (Pendahuluan, CP & TP, Aktivitas 1, Aktivitas 2, Evaluasi).
 */
@Serializable
@Entity(tableName = "progress", primaryKeys = ["userId", "activityId"])
data class ProgressEntity(
    val userId: Int,
    val activityId: String, // "PENDAHULUAN", "CP_TP", "AKTIVITAS_1", "AKTIVITAS_2", "EVALUASI"
    val status: String,     // "BELUM", "PROSES", "SELESAI"
    val progress: Int,      // 0..100
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 3. ANSWER ENTITY
 * Menyimpan setiap jawaban peserta didik (Pertanyaan Aktivitas 1, Tabel Biologi, Aktivitas 2, PG, Uraian)
 * beserta skor, feedback AI, dan skor manual Guru.
 */
@Serializable
@Entity(tableName = "answers", primaryKeys = ["userId", "questionId"])
data class AnswerEntity(
    val userId: Int,
    val questionId: String,
    val answer: String,
    val aiFeedback: String = "",
    val aiSuggestedScore: Int? = null,
    val teacherScore: Int? = null, // Skala 0-100 atau per soal uraian
    val teacherNote: String = "",
    val isSynced: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 4. IMAGE ENTITY
 * Menyimpan foto hasil eksplorasi/visualisasi mandiri morfologi Parijoto.
 */
@Serializable
@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val filePath: String,
    val caption: String,
    val bagianTanaman: String = "Buah & Daun Parijoto",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 5. RESULT ENTITY
 * Menyimpan hasil evaluasi serta riwayat pengerjaan peserta didik.
 */
@Serializable
@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val namaSiswa: String,
    val nisSiswa: String,
    val kelasSiswa: String,
    val kelompokSiswa: String,
    val mcqCorrectCount: Int,
    val mcqTotalCount: Int,
    val mcqScore: Float,        // 0..100
    val essayScore: Float?,     // Null jika belum dinilai Guru/AI
    val finalScore: Float,      // Nilai gabungan sesuai bobot
    val gradingMode: String,    // "MENUNGGU_GURU", "DINILAI_AI", "DINILAI_GURU"
    val rubricSummaryJson: String = "",
    val teacherFeedback: String = "",
    val date: Long = System.currentTimeMillis()
)

/**
 * 6. TEACHER CONFIG ENTITY
 * Memungkinkan Guru mengubah konten, identitas sekolah, URL video, URL referensi, QR, dan bobot nilai tanpa hardcode.
 */
@Serializable
@Entity(tableName = "teacher_config")
data class TeacherConfigEntity(
    @PrimaryKey val configId: Int = 1,
    val schoolName: String = "SMA Negeri 1 Gunung Muria",
    val moduleTitle: String = "KEANEKARAGAMAN HAYATI: EKSPLORASI ETNOSAINS PARIJOTO",
    val authorName: String = "Ahzami, S.Pd., Gr",
    val moduleYear: String = "2026",
    val classPhase: String = "X / Fase E (2 x 45 Menit)",
    val videoUrlAktivitas1: String = "https://www.youtube.com/watch?v=parijoto_muria_etnosains",
    val refUrlAktivitas1: String = "https://id.wikipedia.org/wiki/Parijoto",
    val qrCodeAktivitas1: String = "ELKPD-PARIJOTO-MURIA-AKT1",
    val refUrlIndikasiGeografis: String = "https://dgip.go.id/indikasi-geografis-parijoto-muria",
    val qrCodeAktivitas2: String = "ELKPD-PARIJOTO-IG-KONSERVASI",
    val mcqWeightPercent: Int = 50,
    val essayWeightPercent: Int = 50,
    val pointsPerMcq: Int = 20,
    val isAiEnabled: Boolean = true,
    val teacherPin: String = "2026",
    // Bobot 6 aspek rubrik uraian (skala 1-4)
    val weightKonsepBiologi: Int = 20,
    val weightPenggunaanData: Int = 15,
    val weightKemampuanAnalisis: Int = 20,
    val weightBerpikirKritis: Int = 15,
    val weightSolusiKonservasi: Int = 15,
    val weightKomunikasi: Int = 15
)

/**
 * Model Soal Pilihan Ganda HOTS
 */
@Serializable
data class McqQuestion(
    val id: String,
    val number: Int,
    val pblPhase: Int,
    val indicator: String,
    val stimulus: String,
    val question: String,
    val options: Map<String, String>, // "A".."E"
    val correctOption: String,
    val explanation: String
)

/**
 * Model Soal Uraian
 */
@Serializable
data class EssayQuestion(
    val id: String,
    val number: Int,
    val categoryTitle: String,
    val question: String,
    val rubricFocus: String,
    val minWordsHint: Int = 15
)

/**
 * Model Fase Problem Based Learning (PBL)
 */
data class PblPhaseInfo(
    val phaseNumber: Int,
    val shortTitle: String,
    val fullTitle: String,
    val description: String
)

/**
 * Model Dimensi Analisis Biologi (Tabel Interaktif Aktivitas 2)
 */
data class BiologyDimensionRow(
    val id: String,
    val dimensionName: String,
    val hintLokal: String,
    val hintIlmiah: String,
    val hintManfaat: String,
    val hintBudidaya: String,
    val hintIndikator: String
)
