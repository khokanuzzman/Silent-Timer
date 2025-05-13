package com.example.silenttimer.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.PowerManager
import android.util.Log
import com.example.silenttimer.util.NotificationHelper

class RestoreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("RestoreReceiver", "🔔 RestoreReceiver triggered")

        // ✅ WakeLock to ensure execution
        try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "silent-timer::restore"
            )
            wakeLock.acquire(3000L) // Hold for 3 seconds
            wakeLock.release()
        } catch (e: Exception) {
            Log.e("RestoreReceiver", "❌ WakeLock failed: ${e.localizedMessage}")
        }

        // ✅ Set ringer mode to normal
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL

        // ✅ Show confirmation notification
        NotificationHelper.showNotification(
            context,
            title = "Silent Timer",
            message = "Device restored to normal mode",
            id = 1002
        )

        Log.d("RestoreReceiver", "✅ Ringer mode set to NORMAL")
    }
}
