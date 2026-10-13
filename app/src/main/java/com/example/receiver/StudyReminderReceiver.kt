package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.util.NotificationHelper

class StudyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = "Waktunya Belajar Etnosains Parijoto! 🍇"
        val message = "Mari tingkatkan skor HOTS dan selesaikan tantangan praktikum hari ini."

        NotificationHelper.showStudyReminderNotification(context, title, message)
    }
}
