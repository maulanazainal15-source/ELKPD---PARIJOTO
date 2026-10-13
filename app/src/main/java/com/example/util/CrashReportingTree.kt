package com.example.util

import android.util.Log

class CrashReportingTree {

    fun log(priority: Int, tag: String?, message: String, t: Throwable? = null) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        // Simulasi pengiriman Crash Log ke Remote Analytics Service
        if (t != null) {
            if (priority == Log.ERROR) {
                // Log fatal exception ke Dashboard Telemetri
                System.err.println("CRASH REPORTED [$tag]: ${t.localizedMessage ?: message}")
            }
        }
    }
}
