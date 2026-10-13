package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.AnswerEntity
import com.example.model.ProgressEntity
import com.example.model.ResultEntity
import com.example.model.TeacherConfigEntity
import com.example.model.UserEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportGenerator {

    fun generateAndShareStudentReportPdf(
        context: Context,
        user: UserEntity,
        config: TeacherConfigEntity,
        progressList: List<ProgressEntity>,
        answersMap: Map<String, AnswerEntity>,
        latestResult: ResultEntity?
    ): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // A4 width in points (72 dpi)
            val pageHeight = 842 // A4 height in points

            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas: Canvas = page.canvas
            var yPos = 45f
            val margin = 40f
            val contentWidth = pageWidth - (margin * 2)

            val titlePaint = Paint().apply {
                color = Color.rgb(27, 67, 50)
                textSize = 15f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val subHeaderPaint = Paint().apply {
                color = Color.rgb(100, 18, 32)
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bodyBoldPaint = Paint().apply {
                color = Color.rgb(20, 35, 25)
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bodyPaint = Paint().apply {
                color = Color.rgb(40, 55, 45)
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val linePaint = Paint().apply {
                color = Color.rgb(180, 205, 190)
                strokeWidth = 1f
            }

            fun checkNewPage(requiredSpace: Float) {
                if (yPos + requiredSpace > pageHeight - 45f) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = 45f
                }
            }

            fun drawWrappedText(text: String, paint: Paint, indent: Float = 0f) {
                val maxWidth = contentWidth - indent
                val paragraphs = text.split("\n")
                for (paragraph in paragraphs) {
                    val words = paragraph.split(" ")
                    var currentLine = ""
                    for (word in words) {
                        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                        if (paint.measureText(testLine) <= maxWidth) {
                            currentLine = testLine
                        } else {
                            checkNewPage(16f)
                            canvas.drawText(currentLine, margin + indent, yPos, paint)
                            yPos += 14f
                            currentLine = word
                        }
                    }
                    if (currentLine.isNotEmpty()) {
                        checkNewPage(16f)
                        canvas.drawText(currentLine, margin + indent, yPos, paint)
                        yPos += 14f
                    }
                }
            }

            // HEADER LAPORAN E-LKPD
            canvas.drawText("LAPORAN HASIL PEMBELAJARAN E-LKPD BIOLOGI", margin, yPos, titlePaint)
            yPos += 16f
            canvas.drawText("Eksplorasi Etnosains Parijoto (Medinilla speciosa) — Kelas X / Fase E", margin, yPos, subHeaderPaint)
            yPos += 14f
            canvas.drawText("Sekolah: ${config.schoolName} | Penulis: ${config.authorName} (${config.moduleYear})", margin, yPos, bodyPaint)
            yPos += 10f
            canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint)
            yPos += 18f

            // 1. IDENTITAS PESERTA DIDIK
            canvas.drawText("A. IDENTITAS PESERTA DIDIK", margin, yPos, subHeaderPaint)
            yPos += 15f
            val dateStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
            canvas.drawText("Nama Lengkap : ${user.nama}", margin, yPos, bodyBoldPaint)
            canvas.drawText("Kelas / Fase : ${user.kelas}", margin + 270f, yPos, bodyBoldPaint)
            yPos += 14f
            canvas.drawText("NIS          : ${user.nis}", margin, yPos, bodyPaint)
            canvas.drawText("Kelompok     : ${user.kelompok} (${user.modeKerja})", margin + 270f, yPos, bodyPaint)
            yPos += 14f
            canvas.drawText("Tanggal Cetak: $dateStr", margin, yPos, bodyPaint)
            yPos += 14f
            canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint)
            yPos += 18f

            // 2. RINGKASAN PROGRESS & NILAI EVALUASI
            canvas.drawText("B. CAPAIAN PROGRESS & HASIL EVALUASI", margin, yPos, subHeaderPaint)
            yPos += 15f
            val avgProgress = if (progressList.isEmpty()) 0 else progressList.sumOf { it.progress } / 5
            canvas.drawText("Total Penyelesaian E-LKPD: $avgProgress%", margin, yPos, bodyBoldPaint)
            yPos += 14f

            if (latestResult != null) {
                canvas.drawText(
                    "Nilai Pilihan Ganda HOTS : ${String.format(Locale.US, "%.1f", latestResult.mcqScore)} (${latestResult.mcqCorrectCount}/${latestResult.mcqTotalCount} Benar)",
                    margin,
                    yPos,
                    bodyPaint
                )
                yPos += 14f
                val essayLabel = latestResult.essayScore?.let { String.format(Locale.US, "%.1f", it) } ?: "Menunggu Penilaian Guru"
                canvas.drawText("Nilai Soal Uraian        : $essayLabel (Status: ${latestResult.gradingMode})", margin, yPos, bodyPaint)
                yPos += 14f
                canvas.drawText("NILAI AKHIR GABUNGAN     : ${String.format(Locale.US, "%.1f", latestResult.finalScore)}", margin, yPos, bodyBoldPaint)
                yPos += 14f
                if (latestResult.teacherFeedback.isNotBlank()) {
                    drawWrappedText("Catatan / Feedback Guru: ${latestResult.teacherFeedback}", bodyPaint)
                }
            } else {
                canvas.drawText("Evaluasi belum diselesaikan.", margin, yPos, bodyPaint)
                yPos += 14f
            }

            yPos += 6f
            canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint)
            yPos += 18f

            // 3. JAWABAN AKTIVITAS 1 & AKTIVITAS 2
            checkNewPage(40f)
            canvas.drawText("C. REKAMAN JAWABAN AKTIVITAS 1 & AKTIVITAS 2 (PBL)", margin, yPos, subHeaderPaint)
            yPos += 16f

            val activityKeys = listOf(
                "akt1_k1_q1" to "Aktivitas 1 (Kegiatan 1.1) - Kaitan Nilai Ekonomi & Populasi Alami",
                "akt1_k1_q2" to "Aktivitas 1 (Kegiatan 1.2) - Konsekuensi Ekologis Kepunahan Lokal",
                "akt1_k2_q1" to "Aktivitas 1 (Kegiatan 2.1) - Konsep Etnosains Parijoto",
                "akt1_k2_q2" to "Aktivitas 1 (Kegiatan 2.2) - Integrasi Kearifan Lokal & Literasi Konservasi",
                "akt1_k3_hipotesis" to "Aktivitas 1 (Kegiatan 3) - Perumusan Hipotesis Budidaya",
                "akt2_rekonstruksi" to "Aktivitas 2 - Rekonstruksi Etnosains & Validasi Ilmiah",
                "akt2_studi_ig" to "Aktivitas 2 - Studi Literatur Status Indikasi Geografis (IG)",
                "akt2_senyawa" to "Aktivitas 2 - Analisis Senyawa Antioksidan Ilmiah",
                "akt2_kritis_1" to "Aktivitas 2 - Analisis Kritis Transisi ke Budidaya Mandiri",
                "akt2_kritis_2" to "Aktivitas 2 - Kearifan Lokal Muria & Biodiversitas Global",
                "akt2_kesimpulan" to "Aktivitas 2 - Kesimpulan Integrasi Etnosains & Biologi"
            )

            for ((key, title) in activityKeys) {
                checkNewPage(35f)
                canvas.drawText("• $title:", margin, yPos, bodyBoldPaint)
                yPos += 13f
                val ans = answersMap[key]?.answer?.ifBlank { "(Belum diisi)" } ?: "(Belum diisi)"
                drawWrappedText(ans, bodyPaint, indent = 10f)
                yPos += 6f
            }

            // 4. JAWABAN SOAL URAIAN & RUBRIK
            checkNewPage(45f)
            canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint)
            yPos += 16f
            canvas.drawText("D. JAWABAN SOAL URAIAN & EVALUASI RUBRIK", margin, yPos, subHeaderPaint)
            yPos += 16f

            val essayKeys = listOf(
                "essay_1" to "Uraian 1: Interpretasi Fenomena Ekologis",
                "essay_2" to "Uraian 2: Evaluasi Status Konservasi",
                "essay_3" to "Uraian 3: Sintesis Solusi Pelestarian",
                "essay_4" to "Uraian 4: Komunikasi Digital & Literasi"
            )

            for ((key, title) in essayKeys) {
                checkNewPage(40f)
                canvas.drawText("• $title:", margin, yPos, bodyBoldPaint)
                yPos += 13f
                val entity = answersMap[key]
                val ansText = entity?.answer?.ifBlank { "(Belum dijawab)" } ?: "(Belum dijawab)"
                drawWrappedText(ansText, bodyPaint, indent = 10f)
                if (entity?.teacherScore != null) {
                    drawWrappedText("Skor Guru: ${entity.teacherScore}/100 | Catatan: ${entity.teacherNote}", bodyBoldPaint, indent = 10f)
                } else if (entity?.aiSuggestedScore != null) {
                    drawWrappedText("Estimasi Rubrik AI: ${entity.aiSuggestedScore}/100", bodyBoldPaint, indent = 10f)
                }
                yPos += 6f
            }

            // 5. BOBOT RUBRIK PENILAIAN
            checkNewPage(50f)
            canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint)
            yPos += 16f
            canvas.drawText("E. STANDAR RUBRIK PENILAIAN URAIAN (SKALA 1-4)", margin, yPos, subHeaderPaint)
            yPos += 14f
            drawWrappedText(
                "1. Ketepatan Konsep Biologi (${config.weightKonsepBiologi}%) | " +
                        "2. Penggunaan Data (${config.weightPenggunaanData}%) | " +
                        "3. Kemampuan Analisis (${config.weightKemampuanAnalisis}%) | " +
                        "4. Berpikir Kritis (${config.weightBerpikirKritis}%) | " +
                        "5. Solusi Konservasi (${config.weightSolusiKonservasi}%) | " +
                        "6. Komunikasi Ilmiah (${config.weightKomunikasi}%)",
                bodyPaint
            )

            pdfDocument.finishPage(page)

            val safeName = user.nama.replace(Regex("[^a-zA-Z0-9]"), "_").ifBlank { "Siswa" }
            val outFile = File(context.filesDir, "ELKPD_Parijoto_${safeName}_${user.nis}.pdf")
            FileOutputStream(outFile).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()
            outFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun sharePdfFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Laporan Hasil E-LKPD Etnosains Parijoto")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan / Cetak PDF Hasil E-LKPD"))
    }
}
