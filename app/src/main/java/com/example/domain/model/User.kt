package com.example.domain.model

data class User(
    val id: Int = 0,
    val nama: String = "",
    val nis: String = "",
    val kelas: String = "",
    val kelompok: String = "",
    val mode: UserMode = UserMode.KELOMPOK,
    val role: UserRole = UserRole.STUDENT,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val fullName: String = nama,
    val schoolName: String = kelompok.ifBlank { "SMA/MA Gunung Muria" },
    val className: String = kelas,
    val nisn: String = nis
)

enum class UserMode {
    INDIVIDU,
    KELOMPOK
}

enum class UserRole {
    STUDENT,
    TEACHER
}
