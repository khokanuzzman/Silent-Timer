package com.example.silenttimer.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.silenttimer.data.model.SilentPeriod
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScheduleCountdown(period: SilentPeriod) {
    val startCal = remember(period.startTime) { parseTimeToCalendar(period.startTime) }
    val endCal = remember(period.endTime) { parseTimeToCalendar(period.endTime) }
    var label by remember { mutableStateOf("") }

    LaunchedEffect(period.id) {
        while (true) {
            val now = Calendar.getInstance().timeInMillis
            label = when {
                startCal != null && endCal != null && now in startCal.timeInMillis..endCal.timeInMillis -> {
                    val diff = endCal.timeInMillis - now
                    val h = (diff / (1000 * 60 * 60)) % 24
                    val m = (diff / (1000 * 60)) % 60
                    val s = (diff / 1000) % 60
                    "⏳ Ends in %02d:%02d:%02d".format(h, m, s)
                }
                startCal != null && now < startCal.timeInMillis -> {
                    val diff = startCal.timeInMillis - now
                    val h = (diff / (1000 * 60 * 60)) % 24
                    val m = (diff / (1000 * 60)) % 60
                    val s = (diff / 1000) % 60
                    "🕒 Starts in %02d:%02d:%02d".format(h, m, s)
                }
                else -> ""
            }
            delay(1000)
        }
    }

    if (label.isNotBlank()) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

// Helper function to parse HH:mm time string into Calendar
fun parseTimeToCalendar(time: String): Calendar? {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = format.parse(time)
        Calendar.getInstance().apply {
            if (date != null) {
                set(Calendar.HOUR_OF_DAY, date.hours)
                set(Calendar.MINUTE, date.minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
            }
        }
    } catch (e: Exception) {
        null
    }
}
