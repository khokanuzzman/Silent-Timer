package com.example.silenttimer.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import com.example.silenttimer.util.NotificationHelper

class SilentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, receivedIntent: Intent) {
        Log.d("SilentReceiver", "🚨 Triggered with intent: $receivedIntent")

        val mode = receivedIntent.getStringExtra("mode") ?: "SILENT"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !notificationManager.isNotificationPolicyAccessGranted
        ) {
            val intentToSettings = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intentToSettings)
            Log.w("SilentReceiver", "⚠️ DND permission not granted. Redirecting to settings.")
            return
        }

        try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "silent-timer::alarm"
            )
            wakeLock.acquire(3000L)
            wakeLock.release()
        } catch (e: Exception) {
            Log.e("SilentReceiver", "WakeLock failed: ${e.localizedMessage}")
        }

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val newMode = when (mode.uppercase()) {
            "VIBRATE" -> AudioManager.RINGER_MODE_VIBRATE
            else -> AudioManager.RINGER_MODE_SILENT
        }

        audioManager.ringerMode = newMode

        NotificationHelper.showNotification(
            context,
            title = "Silent Timer",
            message = "Device is now in $mode mode",
            id = 1001
        )

        Log.d("SilentReceiver", "✅ Ringer mode set to: $mode")
    }
}
