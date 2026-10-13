package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.StudentSubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentSubmissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: StudentSubmissionEntity)

    @Query("SELECT * FROM student_submissions ORDER BY submittedAt DESC")
    fun getAllSubmissions(): Flow<List<StudentSubmissionEntity>>

    @Query("SELECT * FROM student_submissions WHERE submissionType = :type ORDER BY submittedAt DESC")
    fun getSubmissionsByType(type: String): Flow<List<StudentSubmissionEntity>>

    @Query("SELECT * FROM student_submissions WHERE userId = :userId ORDER BY submittedAt DESC")
    fun getSubmissionsByUser(userId: Int): Flow<List<StudentSubmissionEntity>>

    @Query("SELECT * FROM student_submissions WHERE id = :id LIMIT 1")
    suspend fun getSubmissionById(id: String): StudentSubmissionEntity?

    @Query("DELETE FROM student_submissions WHERE id = :id")
    suspend fun deleteSubmission(id: String)

    @Query("UPDATE student_submissions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("SELECT COUNT(*) FROM student_submissions")
    suspend fun getCount(): Int
}
