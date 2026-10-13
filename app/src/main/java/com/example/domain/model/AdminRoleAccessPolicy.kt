package com.example.domain.model

/**
 * Pengaturan & Ketentuan Akses Peran Pengguna (Siswa & Guru/Administrator)
 * yang dikelola melalui Dashboard Admin.
 */
data class AdminRoleAccessPolicy(
    val adminPin: String = "2026",
    val requireAdminPinForLogin: Boolean = true,
    val requireApprovedNipPrefix: Boolean = false,
    val approvedNipPrefix: String = "19",
    val allowStudentSelfRegistration: Boolean = true,
    val requireClassAccessCode: Boolean = false,
    val classAccessCode: String = "MURIA2026",
    val allowedStudentClasses: String = "X-1, X-2, X-3, X E-1, X E-2",
    val allowIndividuMode: Boolean = true,
    val allowKelompokMode: Boolean = true,
    val minimumPassingGradeKkm: Int = 75,
    val isEvaluationOpen: Boolean = true,
    val isAiTutorEnabled: Boolean = true,
    val schoolName: String = "SMA Negeri 1 Gunung Muria",
    val academicYear: String = "2026/2027",
    val announcementBanner: String = "Pastikan membaca rubrik HOTS sebelum mengerjakan Evaluasi Akhir."
) {
    fun getAllowedClassesList(): List<String> =
        allowedStudentClasses.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
