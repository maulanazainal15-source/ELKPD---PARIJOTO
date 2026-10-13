package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.AnswerDao
import com.example.data.local.dao.BibliographyDao
import com.example.data.local.dao.ConfigDao
import com.example.data.local.dao.DynamicContentDao
import com.example.data.local.dao.ImageDao
import com.example.data.local.dao.ProgressDao
import com.example.data.local.dao.ResultDao
import com.example.data.local.dao.StudentSubmissionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AnswerEntity
import com.example.data.local.entity.BibliographyEntity
import com.example.data.local.entity.ConfigEntity
import com.example.data.local.entity.DynamicContentEntity
import com.example.data.local.entity.ImageEntity
import com.example.data.local.entity.ProgressEntity
import com.example.data.local.entity.ResultEntity
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ProgressEntity::class,
        AnswerEntity::class,
        ImageEntity::class,
        ResultEntity::class,
        ConfigEntity::class,
        DynamicContentEntity::class,
        StudentSubmissionEntity::class,
        BibliographyEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun progressDao(): ProgressDao
    abstract fun answerDao(): AnswerDao
    abstract fun imageDao(): ImageDao
    abstract fun resultDao(): ResultDao
    abstract fun configDao(): ConfigDao
    abstract fun dynamicContentDao(): DynamicContentDao
    abstract fun studentSubmissionDao(): StudentSubmissionDao
    abstract fun bibliographyDao(): BibliographyDao
}
