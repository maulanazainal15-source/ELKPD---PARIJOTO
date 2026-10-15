package com.example.data.repository

import android.os.Build
import android.util.Log
import com.example.data.local.dao.StudentSubmissionDao
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.domain.model.StudentSubmission
import com.example.domain.model.SubmissionType
import com.example.domain.repository.StudentSubmissionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StudentSubmissionRepositoryImpl(
    private val dao: StudentSubmissionDao,
    private val firestore: FirebaseFirestore? = null,
    private val auth: FirebaseAuth? = null
) : StudentSubmissionRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var snapshotListener: ListenerRegistration? = null

    init {
        startRealtimeSync()
    }

    private fun startRealtimeSync() {
        val fs = firestore ?: return
        try {
            snapshotListener = fs.collection("student_submissions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("SubmissionRepo", "Firestore snapshot listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch {
                            val entities = snapshot.documents.mapNotNull { doc ->
                                try {
                                    val id = doc.getString("id") ?: doc.id
                                    val studentName = doc.getString("studentName") ?: "Siswa"
                                    val studentClass = doc.getString("studentClass") ?: "X E-1"
                                    val studentNis = doc.getString("studentNis") ?: ""
                                    val subType = doc.getString("submissionType") ?: SubmissionType.ACTIVITY_1.name
                                    val title = doc.getString("title") ?: "Tugas E-LKPD"
                                    val summary = doc.getString("summary") ?: ""
                                    val payload = doc.getString("payloadJson") ?: "{}"
                                    val score = doc.getDouble("score")?.toFloat()
                                    val status = doc.getString("status") ?: "TERKIRIM"
                                    val school = doc.getString("schoolName") ?: "SMA Negeri 1 Gunung Muria"
                                    val device = doc.getString("deviceInfo") ?: ""

                                    val timestamp = doc.getTimestamp("submissionTimestamp")?.toDate()?.time
                                        ?: System.currentTimeMillis()

                                    StudentSubmissionEntity(
                                        id = id,
                                        userId = id.hashCode(),
                                        studentName = studentName,
                                        studentClass = studentClass,
                                        studentNis = studentNis,
                                        submissionType = subType,
                                        title = title,
                                        summary = summary,
                                        payloadJson = payload,
                                        score = score,
                                        status = status,
                                        submittedAt = timestamp,
                                        schoolName = school,
                                        deviceInfo = device
                                    )
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            entities.forEach { dao.insertSubmission(it) }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("SubmissionRepo", "Gagal memulai realtime snapshot sync: ${e.message}", e)
        }
    }

    override fun getAllSubmissions(): Flow<List<StudentSubmission>> {
        return dao.getAllSubmissions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSubmissionsByType(type: SubmissionType): Flow<List<StudentSubmission>> {
        return dao.getSubmissionsByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSubmissionsByUser(userId: Int): Flow<List<StudentSubmission>> {
        return dao.getSubmissionsByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSubmissionById(id: String): StudentSubmission? {
        return dao.getSubmissionById(id)?.toDomain()
    }

    override suspend fun submitPayload(submission: StudentSubmission) {
        val currentDevice = submission.deviceInfo.ifBlank {
            "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"
        }
        val completeSubmission = submission.copy(deviceInfo = currentDevice)

        // 1. Local Offline-First Cache
        dao.insertSubmission(StudentSubmissionEntity.fromDomain(completeSubmission))

        // 2. Upload to Cloud Firestore
        val fs = firestore
        if (fs != null) {
            try {
                val studentId = auth?.currentUser?.uid ?: "student_${submission.userId}"
                val docMap = hashMapOf<String, Any>(
                    "id" to completeSubmission.id,
                    "studentId" to studentId,
                    "studentName" to completeSubmission.studentName,
                    "schoolName" to completeSubmission.schoolName,
                    "studentClass" to completeSubmission.studentClass,
                    "studentNis" to completeSubmission.studentNis,
                    "submissionType" to completeSubmission.submissionType.name,
                    "title" to completeSubmission.title,
                    "summary" to completeSubmission.summary,
                    "payloadJson" to completeSubmission.payloadJson,
                    "score" to (completeSubmission.score ?: 0f),
                    "status" to completeSubmission.status,
                    "submissionTimestamp" to FieldValue.serverTimestamp(),
                    "deviceInfo" to completeSubmission.deviceInfo
                )
                fs.collection("student_submissions")
                    .document(completeSubmission.id)
                    .set(docMap, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                Log.w("SubmissionRepo", "Gagal unggah ke Firestore (offline caching tetap aktif): ${e.message}")
            }
        }
    }

    override suspend fun updateSubmissionStatus(id: String, status: String) {
        dao.updateStatus(id, status)
        val fs = firestore
        if (fs != null) {
            try {
                fs.collection("student_submissions").document(id)
                    .update("status", status)
                    .await()
            } catch (e: Exception) {
                Log.w("SubmissionRepo", "Gagal memperbarui status ke Firestore: ${e.message}")
            }
        }
    }

    override suspend fun deleteSubmission(id: String) {
        dao.deleteSubmission(id)
        val fs = firestore
        if (fs != null) {
            try {
                fs.collection("student_submissions").document(id).delete().await()
            } catch (e: Exception) {
                Log.w("SubmissionRepo", "Gagal menghapus submisi di Firestore: ${e.message}")
            }
        }
    }

    override suspend fun getCount(): Int {
        return dao.getCount()
    }
}
