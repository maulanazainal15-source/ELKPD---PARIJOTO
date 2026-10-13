package com.example.domain.repository

import com.example.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerOrLoginUser(user: User): Long
    suspend fun insertOrUpdateStudent(user: User): Long
    suspend fun getUserById(userId: Int): User?
    suspend fun getUserByNis(nis: String): User?
    fun getAllStudents(): Flow<List<User>>
    fun getAllUsers(): Flow<List<User>>
    suspend fun deleteUser(userId: Int)
}
