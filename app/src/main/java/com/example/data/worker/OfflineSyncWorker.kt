package com.example.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class OfflineSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Ambil antrean data lokal yang pending disinkronkan (Jurnal, Quiz, Forum)
            // 2. Kirim payload via API Client / Remote Repository
            delay(2000L) // Simulasi transmisi network

            // 3. Update status data lokal menjadi IS_SYNCED = true
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
