package com.example.silenttimer.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.silenttimer.data.model.SilentPeriod
import java.text.SimpleDateFormat
import java.util.*

object SilentModeScheduler {

    fun scheduleSilentPeriod(context: Context, period: SilentPeriod) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // ✅ Android 12+ exact alarm permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                Log.w("SilentModeScheduler", "🚫 Exact alarm permission not granted. Redirecting to settings.")
                return
            }
        }

        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()
        val startTime = formatter.parse(period.startTime)
        val endTime = formatter.parse(period.endTime)

        if (startTime == null || endTime == null) {
            Log.e("SilentModeScheduler", "❌ Invalid start or end time format")
            return
        }

        val startCal = Calendar.getInstance().apply {
            time = startTime
            set(Calendar.YEAR, now.get(Calendar.YEAR))
            set(Calendar.MONTH, now.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DATE, 1)
        }

        val endCal = Calendar.getInstance().apply {
            time = endTime
            set(Calendar.YEAR, now.get(Calendar.YEAR))
            set(Calendar.MONTH, now.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DATE, 1)
        }

        Log.d("SilentModeScheduler", "📅 Start at: ${startCal.time}, End at: ${endCal.time}")

        val startIntent = Intent(context, SilentReceiver::class.java).apply {
            putExtra("mode", period.mode)
        }
        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            period.id,
            startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            startCal.timeInMillis,
            startPendingIntent
        )
        Log.d("SilentModeScheduler", "✅ Start alarm set for ${startCal.time} with ID: ${period.id}")

        val endIntent = Intent(context, RestoreReceiver::class.java)
        val endPendingIntent = PendingIntent.getBroadcast(
            context,
            -period.id,
            endIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            endCal.timeInMillis,
            endPendingIntent
        )
        Log.d("SilentModeScheduler", "✅ End alarm set for ${endCal.time} with ID: ${-period.id}")
    }

    fun cancelScheduledAlarms(context: Context, periodId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startIntent = Intent(context, SilentReceiver::class.java)
        val endIntent = Intent(context, RestoreReceiver::class.java)

        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            periodId,
            startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val endPendingIntent = PendingIntent.getBroadcast(
            context,
            -periodId,
            endIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(startPendingIntent)
        alarmManager.cancel(endPendingIntent)

        Log.d("SilentModeScheduler", "🛑 Cancelled alarms for ID: $periodId")
    }
}