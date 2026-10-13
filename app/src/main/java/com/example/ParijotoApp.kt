package com.example

import android.app.Application
import com.example.di.DatabaseModule
import com.example.util.CrashReportingTree
import com.example.util.NotificationHelper

/**
 * File utama turunan Application untuk menginisialisasi arsitektur aplikasi,
 * DatabaseModule DI, Notification Channel, dan CrashReportingTree.
 */
class ParijotoApp : Application() {

    lateinit var crashReportingTree: CrashReportingTree
        private set

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi konfigurasi global & modul database
        DatabaseModule.initialize(this)
        // Buat channel notifikasi pengingat belajar harian (Phase 17)
        NotificationHelper.createNotificationChannel(this)
        // Inisialisasi CrashReportingTree untuk pemantauan diagnostik (Phase 17/18)
        crashReportingTree = CrashReportingTree()
    }
}
