package com.example.silenttimer.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.silenttimer.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getInstance(context)
                val allPeriods = db.silentPeriodDao().getAllNow()

                allPeriods.forEach { period ->
                    SilentModeScheduler.scheduleSilentPeriod(context, period)
                }
            }
        }
    }
}
