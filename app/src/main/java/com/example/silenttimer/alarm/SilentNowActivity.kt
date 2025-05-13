package com.example.silenttimer.alarm

import android.app.Activity
import android.media.AudioManager
import android.os.Bundle
import android.widget.Toast
import com.example.silenttimer.util.NotificationHelper

class SilentNowActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT

        NotificationHelper.showNotification(
            context = this,
            title = "Silent Timer",
            message = "Device set to Silent Mode",
            id = 2002
        )

        Toast.makeText(this, "Silent Mode Activated", Toast.LENGTH_SHORT).show()
        finish()
    }
}
