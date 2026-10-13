package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["nis"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val nis: String,
    val kelas: String,
    val kelompok: String,
    val mode: String, // "INDIVIDU" atau "KELOMPOK"
    val role: String = "STUDENT", // "STUDENT" atau "TEACHER"
    val createdTimestamp: Long = System.currentTimeMillis()
)
