package com.example.data.remote

import com.example.data.remote.dto.StudentSyncDto

class CloudSyncService {
    suspend fun uploadStudentData(syncData: StudentSyncDto): Result<Boolean> {
        return try {
            // Logika pengiriman HTTP POST/PUT ke Supabase / Firebase Rest API
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
