package com.example.silenttimer.util

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log

object DndPermissionHelper {

    fun isDndAccessGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun requestDndPermissionIfNeeded(context: Context) {
        if (isDndAccessGranted(context)) return

        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val askedBefore = prefs.getBoolean("dnd_requested", false)
        if (askedBefore) return

        prefs.edit().putBoolean("dnd_requested", true).apply()

        AlertDialog.Builder(context)
            .setTitle("Permission Needed")
            .setMessage("To enable silent or vibrate mode on schedule, please allow Do Not Disturb access.")
            .setPositiveButton("Go to Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("DndPermissionHelper", "Failed to open settings: ${e.localizedMessage}")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
