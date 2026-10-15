package com.example.di

import android.content.Context
import com.example.data.repository.AdminPolicyRepositoryImpl
import com.example.data.repository.BibliographyRepositoryImpl
import com.example.data.repository.DynamicContentRepositoryImpl
import com.example.data.repository.EvaluationRepositoryImpl
import com.example.data.repository.LearningRepositoryImpl
import com.example.data.repository.StudentSubmissionRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.AdminPolicyRepository
import com.example.domain.repository.BibliographyRepository
import com.example.domain.repository.DynamicContentRepository
import com.example.domain.repository.EvaluationRepository
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.StudentSubmissionRepository
import com.example.domain.repository.UserRepository

/**
 * Modul Penyedia & Pengikat Repository (DI RepositoryModule)
 * Menghubungkan abstraksi interface repository di layer domain ke implementasi konkret di layer data.
 */
object RepositoryModule {

    fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository =
        userRepositoryImpl

    fun bindLearningRepository(learningRepositoryImpl: LearningRepositoryImpl): LearningRepository =
        learningRepositoryImpl

    fun bindEvaluationRepository(evaluationRepositoryImpl: EvaluationRepositoryImpl): EvaluationRepository =
        evaluationRepositoryImpl

    fun bindAdminPolicyRepository(adminPolicyRepositoryImpl: AdminPolicyRepositoryImpl): AdminPolicyRepository =
        adminPolicyRepositoryImpl

    fun bindDynamicContentRepository(dynamicContentRepositoryImpl: DynamicContentRepositoryImpl): DynamicContentRepository =
        dynamicContentRepositoryImpl

    fun bindStudentSubmissionRepository(impl: StudentSubmissionRepositoryImpl): StudentSubmissionRepository =
        impl

    fun bindBibliographyRepository(impl: BibliographyRepositoryImpl): BibliographyRepository =
        impl

    fun provideUserRepository(context: Context): UserRepository {
        val db = DatabaseModule.provideAppDatabase(context)
        return bindUserRepository(UserRepositoryImpl(DatabaseModule.provideUserDao(db)))
    }

    fun provideLearningRepository(context: Context): LearningRepository {
        val db = DatabaseModule.provideAppDatabase(context)
        return bindLearningRepository(
            LearningRepositoryImpl(
                progressDao = DatabaseModule.provideProgressDao(db),
                answerDao = DatabaseModule.provideAnswerDao(db),
                imageDao = DatabaseModule.provideImageDao(db)
            )
        )
    }

    fun provideEvaluationRepository(context: Context): EvaluationRepository {
        val db = DatabaseModule.provideAppDatabase(context)
        return bindEvaluationRepository(
            EvaluationRepositoryImpl(DatabaseModule.provideResultDao(db))
        )
    }

    fun provideAdminPolicyRepository(context: Context): AdminPolicyRepository {
        val db = DatabaseModule.provideAppDatabase(context)
        val firestore = FirebaseModule.provideFirestore(context)
        return bindAdminPolicyRepository(
            AdminPolicyRepositoryImpl(DatabaseModule.provideConfigDao(db), firestore)
        )
    }

    fun provideDynamicContentRepository(context: Context): DynamicContentRepository {
        val db = DatabaseModule.provideAppDatabase(context)
        val firestore = FirebaseModule.provideFirestore(context)
        return bindDynamicContentRepository(
            DynamicContentRepositoryImpl(DatabaseModule.provideDynamicContentDao(db), firestore)
        )
    }

    fun provideStudentSubmissionRepository(context: Context): StudentSubmissionRepository {
        val db = DatabaseModule.provideAppDatabase(context)
        val firestore = FirebaseModule.provideFirestore(context)
        val auth = FirebaseModule.provideFirebaseAuth(context)
        return bindStudentSubmissionRepository(
            StudentSubmissionRepositoryImpl(DatabaseModule.provideStudentSubmissionDao(db), firestore, auth)
        )
    }

    fun provideBibliographyRepository(context: Context): BibliographyRepository {
        val db = DatabaseModule.provideAppDatabase(context)
        val firestore = FirebaseModule.provideFirestore(context)
        return bindBibliographyRepository(
            BibliographyRepositoryImpl(DatabaseModule.provideBibliographyDao(db), firestore)
        )
    }
}
