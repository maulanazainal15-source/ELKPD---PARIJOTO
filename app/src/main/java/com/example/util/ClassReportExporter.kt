package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.domain.model.StudentSummary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ClassReportExporter {

    fun exportAndShareClassCsv(
        context: Context,
        students: List<StudentSummary>
    ) {
        val csvHeader = "No,Nama Lengkap,Sekolah,Kelas,NISN,Progres Act 1 (%),Progres Act 2 (%),Progres Total (%),Skor HOTS,Status Pengerjaan,Status Ketuntasan\n"

        val stringBuilder = StringBuilder(csvHeader)

        students.forEachIndexed { index, student ->
            val hotsStatus = if (student.isHotsSubmitted) "Selesai" else "Belum"
            val statusText = student.status.name

            stringBuilder.append("${index + 1},")
            stringBuilder.append("\"${student.fullName}\",")
            stringBuilder.append("\"${student.schoolName}\",")
            stringBuilder.append("\"${student.className}\",")
            stringBuilder.append("\"${student.nisn}\",")
            stringBuilder.append("${student.act1Percentage},")
            stringBuilder.append("${student.act2Percentage},")
            stringBuilder.append("${student.overallPercentage},")
            stringBuilder.append("${student.hotsScore},")
            stringBuilder.append("$hotsStatus,")
            stringBuilder.append("$statusText\n")
        }

        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) exportDir.mkdirs()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val csvFile = File(exportDir, "Rekap_Nilai_Etnosains_Parijoto_$timeStamp.csv")

        try {
            val outputStream = FileOutputStream(csvFile)
            outputStream.write(stringBuilder.toString().toByteArray())
            outputStream.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                csvFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Rekapitulasi Nilai Etnosains Parijoto - $timeStamp")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Bagikan/Ekspor Rekap Excel (CSV)"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
