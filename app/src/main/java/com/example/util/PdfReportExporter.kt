package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.domain.model.User
import com.example.ui.screens.profile.StudentAnalytics
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportExporter {

    fun generateAndSharePdfReport(
        context: Context,
        user: User,
        analytics: StudentAnalytics
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
        }

        var yPosition = 50f

        // 1. Header Laporan
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        paint.color = Color.parseColor("#1B5E20") // Primary Green Color
        canvas.drawText("RAPOR HASIL BELAJAR ETNOSAINS PARIJOTO", 40f, yPosition, paint)

        yPosition += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textSize = 10f
        paint.color = Color.DKGRAY
        canvas.drawText("Integrasi Sains Lokal Buah Parijoto (Kudus) dalam Pembelajaran IPA/Kimia", 40f, yPosition, paint)

        yPosition += 15f
        paint.strokeWidth = 1.5f
        paint.color = Color.LTGRAY
        canvas.drawLine(40f, yPosition, 555f, yPosition, paint)

        // 2. Data Identitas Siswa
        yPosition += 30f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = Color.BLACK
        canvas.drawText("I. IDENTITAS SISWA", 40f, yPosition, paint)

        yPosition += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val currentDate = dateFormat.format(Date())

        canvas.drawText("Nama Lengkap : ${user.fullName}", 50f, yPosition, paint)
        yPosition += 18f
        canvas.drawText("NISN         : ${user.nisn.ifBlank { "-" }}", 50f, yPosition, paint)
        yPosition += 18f
        canvas.drawText("Kelas        : ${user.className}", 50f, yPosition, paint)
        yPosition += 18f
        canvas.drawText("Sekolah      : ${user.schoolName}", 50f, yPosition, paint)
        yPosition += 18f
        canvas.drawText("Tanggal Cetak: $currentDate", 50f, yPosition, paint)

        // 3. Progres Pembelajaran Etnosains
        yPosition += 35f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        canvas.drawText("II. REKAPITULASI PROGRES BELAJAR", 40f, yPosition, paint)

        yPosition += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f

        canvas.drawText("Aktivitas 1 (Problem-Based Learning) : ${analytics.act1Percentage}% selesai", 50f, yPosition, paint)
        yPosition += 18f
        canvas.drawText("Aktivitas 2 (Praktikum & Rekonstruksi) : ${analytics.act2Percentage}% selesai", 50f, yPosition, paint)
        yPosition += 18f
        canvas.drawText("Total Progres Pembelajaran            : ${analytics.overallLearningPercentage}%", 50f, yPosition, paint)

        // 4. Hasil Evaluasi HOTS
        yPosition += 35f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        canvas.drawText("III. HASIL EVALUASI HOTS (HIGHER ORDER THINKING SKILLS)", 40f, yPosition, paint)

        yPosition += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f

        val statusEval = if (analytics.isHotsSubmitted) "Sudah Selesai" else "Belum Selesai"
        canvas.drawText("Status Pengerjaan Evaluation HOTS : $statusEval", 50f, yPosition, paint)
        yPosition += 18f

        if (analytics.isHotsSubmitted) {
            canvas.drawText("Skor Pilihan Ganda                : ${analytics.hotsPgScore.toInt()} / 100", 50f, yPosition, paint)
            yPosition += 18f

            val predikat = when {
                analytics.hotsPgScore >= 85 -> "Sangat Baik (A)"
                analytics.hotsPgScore >= 75 -> "Baik (B)"
                analytics.hotsPgScore >= 60 -> "Cukup (C)"
                else -> "Perlu Bimbingan (D)"
            }
            canvas.drawText("Kategori Pemahaman HOTS           : $predikat", 50f, yPosition, paint)
        } else {
            canvas.drawText("Skor Pilihan Ganda                : -", 50f, yPosition, paint)
        }

        // 5. Catatan / Catatan Guru
        yPosition += 40f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        canvas.drawText("IV. REKOMENDASI DAN CATATAN", 40f, yPosition, paint)

        yPosition += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textSize = 10f
        paint.color = Color.DKGRAY

        val noteText = if (analytics.overallLearningPercentage >= 100 && analytics.isHotsSubmitted) {
            "Siswa telah menyelesaikan seluruh tahapan modul etnosains Parijoto dengan sangat baik. Pertahankan motivasi belajar sains berbasis kearifan lokal!"
        } else {
            "Siswa disarankan untuk melengkapi modul aktivitas belajar dan evaluasi HOTS yang masih belum penuh."
        }
        canvas.drawText(noteText, 50f, yPosition, paint)

        // Footer Tanda Tangan
        yPosition += 100f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f
        paint.color = Color.BLACK

        canvas.drawText("Guru Pembimbing IPA/Kimia,", 380f, yPosition, paint)
        yPosition += 60f
        canvas.drawText("( ______________________ )", 380f, yPosition, paint)

        pdfDocument.finishPage(page)

        // Menyimpan File PDF dan Membuka Share Intent
        val pdfDir = File(context.cacheDir, "reports")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val fileName = "Rapor_Etnosains_${user.fullName.replace(" ", "_")}.pdf"
        val pdfFile = File(pdfDir, fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Rapor Hasil Belajar Etnosains - ${user.fullName}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Bagikan/Cetak Rapor PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
        }
    }
}
