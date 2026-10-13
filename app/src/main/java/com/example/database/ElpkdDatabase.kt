package com.example.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.model.AnswerEntity
import com.example.model.ImageEntity
import com.example.model.ProgressEntity
import com.example.model.ResultEntity
import com.example.model.TeacherConfigEntity
import com.example.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ElpkdDao {

    // --- USER ---
    @Query("SELECT * FROM users WHERE isActive = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY timestamp DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("UPDATE users SET isActive = 0")
    suspend fun deactivateAllUsers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- PROGRESS ---
    @Query("SELECT * FROM progress WHERE userId = :userId")
    fun getProgressForUser(userId: Int): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ProgressEntity)

    // --- ANSWERS ---
    @Query("SELECT * FROM answers WHERE userId = :userId")
    fun getAnswersForUser(userId: Int): Flow<List<AnswerEntity>>

    @Query("SELECT * FROM answers")
    fun getAllAnswers(): Flow<List<AnswerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAnswer(answer: AnswerEntity)

    @Query("UPDATE answers SET isSynced = 1 WHERE userId = :userId")
    suspend fun markAllAnswersSynced(userId: Int)

    // --- IMAGES ---
    @Query("SELECT * FROM images WHERE userId = :userId ORDER BY timestamp DESC")
    fun getImagesForUser(userId: Int): Flow<List<ImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity): Long

    @Query("DELETE FROM images WHERE id = :imageId")
    suspend fun deleteImageById(imageId: Int)

    // --- RESULTS ---
    @Query("SELECT * FROM results WHERE userId = :userId ORDER BY date DESC")
    fun getResultsForUser(userId: Int): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results ORDER BY date DESC")
    fun getAllResults(): Flow<List<ResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ResultEntity): Long

    @Update
    suspend fun updateResult(result: ResultEntity)

    // --- TEACHER CONFIG ---
    @Query("SELECT * FROM teacher_config WHERE configId = 1 LIMIT 1")
    fun getTeacherConfig(): Flow<TeacherConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTeacherConfig(config: TeacherConfigEntity)
}

@Database(
    entities = [
        UserEntity::class,
        ProgressEntity::class,
        AnswerEntity::class,
        ImageEntity::class,
        ResultEntity::class,
        TeacherConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ElpkdDatabase : RoomDatabase() {
    abstract fun elpkdDao(): ElpkdDao

    companion object {
        @Volatile
        private var INSTANCE: ElpkdDatabase? = null

        fun getDatabase(context: Context): ElpkdDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ElpkdDatabase::class.java,
                    "elkpd_parijoto_muria.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
