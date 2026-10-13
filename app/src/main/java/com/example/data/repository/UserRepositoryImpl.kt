package com.example.data.repository

import com.example.data.local.dao.UserDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun registerOrLoginUser(user: User): Long {
        return userDao.insertUser(user.toEntity())
    }

    override suspend fun insertOrUpdateStudent(user: User): Long {
        return userDao.insertUser(user.toEntity())
    }

    override suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)?.toDomain()
    }

    override suspend fun getUserByNis(nis: String): User? {
        return userDao.getUserByNis(nis)?.toDomain()
    }

    override fun getAllStudents(): Flow<List<User>> {
        return userDao.getAllStudents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteUser(userId: Int) {
        userDao.deleteUserById(userId)
    }
}
