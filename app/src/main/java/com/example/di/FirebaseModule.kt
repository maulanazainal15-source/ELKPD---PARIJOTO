package com.example.di

import android.content.Context
import com.example.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.firestoreSettings

object FirebaseModule {

    @Volatile
    private var firestoreInstance: FirebaseFirestore? = null

    @Volatile
    private var authInstance: FirebaseAuth? = null

    fun provideFirebaseApp(context: Context): FirebaseApp {
        return FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(context)!!
    }

    fun provideFirestore(context: Context): FirebaseFirestore {
        return firestoreInstance ?: synchronized(this) {
            firestoreInstance ?: run {
                provideFirebaseApp(context)
                val databaseId = context.getString(R.string.firestore_database_id)
                val firestore = FirebaseFirestore.getInstance(FirebaseApp.getInstance(), databaseId)
                val settings = firestoreSettings {
                    setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                }
                firestore.firestoreSettings = settings
                firestoreInstance = firestore
                firestore
            }
        }
    }

    fun provideFirebaseAuth(context: Context): FirebaseAuth {
        return authInstance ?: synchronized(this) {
            authInstance ?: run {
                provideFirebaseApp(context)
                val auth = FirebaseAuth.getInstance()
                authInstance = auth
                auth
            }
        }
    }
}
