package com.example.silenttimer.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.example.silenttimer.util.NotificationHelper

class QuickActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.silenttimer.SILENT_NOW") {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT

            NotificationHelper.showNotification(
                context,
                title = "Silent Timer",
                message = "Device set to Silent mode",
                id = 2001
            )
        }
    }
}
