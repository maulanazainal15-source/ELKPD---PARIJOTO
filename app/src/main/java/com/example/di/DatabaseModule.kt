package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.dao.AnswerDao
import com.example.data.local.dao.BibliographyDao
import com.example.data.local.dao.ConfigDao
import com.example.data.local.dao.DynamicContentDao
import com.example.data.local.dao.ImageDao
import com.example.data.local.dao.ProgressDao
import com.example.data.local.dao.ResultDao
import com.example.data.local.dao.StudentSubmissionDao
import com.example.data.local.dao.UserDao

/**
 * Modul Penyedia Database & DAO (DI Provider Module)
 */
object DatabaseModule {

    @Volatile
    private var appDatabaseInstance: AppDatabase? = null

    fun initialize(context: Context) {
        provideAppDatabase(context.applicationContext)
    }

    fun provideAppDatabase(context: Context): AppDatabase {
        return appDatabaseInstance ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "parijoto_ethnoscience.db"
            ).fallbackToDestructiveMigration().build()
            appDatabaseInstance = instance
            instance
        }
    }

    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    fun provideProgressDao(db: AppDatabase): ProgressDao = db.progressDao()

    fun provideAnswerDao(db: AppDatabase): AnswerDao = db.answerDao()

    fun provideImageDao(db: AppDatabase): ImageDao = db.imageDao()

    fun provideResultDao(db: AppDatabase): ResultDao = db.resultDao()

    fun provideConfigDao(db: AppDatabase): ConfigDao = db.configDao()

    fun provideDynamicContentDao(db: AppDatabase): DynamicContentDao = db.dynamicContentDao()

    fun provideStudentSubmissionDao(db: AppDatabase): StudentSubmissionDao = db.studentSubmissionDao()

    fun provideBibliographyDao(db: AppDatabase): BibliographyDao = db.bibliographyDao()
}
