package com.example.data.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.di.DatabaseModule
import com.example.di.FirebaseModule
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await

class OfflineSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = DatabaseModule.provideAppDatabase(applicationContext)
            val submissionDao = DatabaseModule.provideStudentSubmissionDao(db)
            val submissions = submissionDao.getAllSubmissions().firstOrNull() ?: emptyList()

            val firestore = FirebaseModule.provideFirestore(applicationContext)
            val auth = FirebaseModule.provideFirebaseAuth(applicationContext)
            val currentUid = auth.currentUser?.uid ?: "offline_student"

            for (sub in submissions) {
                try {
                    val map = hashMapOf<String, Any>(
                        "id" to sub.id,
                        "studentId" to currentUid,
                        "studentName" to sub.studentName,
                        "schoolName" to sub.schoolName,
                        "studentClass" to sub.studentClass,
                        "studentNis" to sub.studentNis,
                        "submissionType" to sub.submissionType,
                        "title" to sub.title,
                        "summary" to sub.summary,
                        "payloadJson" to sub.payloadJson,
                        "score" to (sub.score ?: 0f),
                        "status" to sub.status,
                        "submissionTimestamp" to FieldValue.serverTimestamp(),
                        "deviceInfo" to sub.deviceInfo
                    )
                    firestore.collection("student_submissions").document(sub.id)
                        .set(map, SetOptions.merge())
                        .await()
                } catch (e: Exception) {
                    Log.w("OfflineSyncWorker", "Failed to sync sub ${sub.id}: ${e.message}")
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("OfflineSyncWorker", "Sync worker exception: ${e.message}", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<OfflineSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
