package com.example.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.remote.CloudSyncService
import com.example.data.remote.dto.StudentSyncDto
import com.example.di.RepositoryModule
import com.example.domain.repository.EvaluationRepository
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val userRepository: UserRepository = RepositoryModule.provideUserRepository(context)
    private val learningRepository: LearningRepository = RepositoryModule.provideLearningRepository(context)
    private val evaluationRepository: EvaluationRepository = RepositoryModule.provideEvaluationRepository(context)
    private val cloudSyncService: CloudSyncService = CloudSyncService()

    override suspend fun doWork(): Result {
        return try {
            val user = userRepository.getAllStudents().firstOrNull()?.lastOrNull()
                ?: return Result.success()

            val act1 = learningRepository.getProgressByUserAndActivity(user.id, "ACT_1").firstOrNull()
            val act2 = learningRepository.getProgressByUserAndActivity(user.id, "ACT_2").firstOrNull()
            val eval = evaluationRepository.getResultByUser(user.id).firstOrNull()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())

            val syncDto = StudentSyncDto(
                studentId = user.id.toString(),
                fullName = user.fullName,
                schoolName = user.schoolName,
                className = user.className,
                act1Percentage = act1?.percentage ?: 0,
                act2Percentage = act2?.percentage ?: 0,
                hotsScore = eval?.pgScore ?: 0.0,
                lastSyncedAt = timestamp
            )

            val syncResult = cloudSyncService.uploadStudentData(syncDto)

            if (syncResult.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun enqueuePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(syncRequest)
        }
    }
}
