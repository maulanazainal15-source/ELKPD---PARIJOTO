package com.example.domain.usecase

import com.example.domain.model.StudentReportExport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.util.Locale

class ExportAnalyticsReportUseCase {

    suspend operator fun invoke(
        targetFile: File,
        studentList: List<StudentReportExport>
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            FileWriter(targetFile).use { writer ->
                // Header CSV
                writer.append("ID Siswa,Nama Siswa,Kelas,Indeks HOTS,Kategori,Rata Quiz,Skor PBL,Skor Forum,Tanggal Selesai\n")

                // Data Baris Siswa
                studentList.forEach { student ->
                    writer.append("${student.studentId},")
                    writer.append("\"${student.studentName}\",")
                    writer.append("${student.classGroup},")
                    writer.append("${String.format(Locale.getDefault(), "%.2f", student.hotsIndex)},")
                    writer.append("${student.category},")
                    writer.append("${String.format(Locale.getDefault(), "%.1f", student.quizAverage)},")
                    writer.append("${String.format(Locale.getDefault(), "%.1f", student.pblScore)},")
                    writer.append("${String.format(Locale.getDefault(), "%.1f", student.forumScore)},")
                    writer.append("${student.completionTimestamp}\n")
                }
            }
            targetFile
        }
    }
}
