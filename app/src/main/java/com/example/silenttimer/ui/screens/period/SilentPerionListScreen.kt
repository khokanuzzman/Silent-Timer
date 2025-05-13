package com.example.silenttimer.ui.screens.period

import EmptySchedulePlaceholder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.silenttimer.data.model.SilentPeriod
import com.example.silenttimer.ui.period.SilentPeriodViewModel
import com.example.silenttimer.alarm.SilentModeScheduler
import androidx.compose.runtime.livedata.observeAsState
import com.example.silenttimer.ui.components.ScheduleCountdown
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SilentPeriodListScreen(
    viewModel: SilentPeriodViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val periods by viewModel.silentPeriods.observeAsState(emptyList())
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Scheduled Silent Periods",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(16.dp))

            if (periods.isEmpty()) {
                EmptySchedulePlaceholder()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(periods) { period ->
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 3.dp,
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Spacer(Modifier.height(6.dp))

                                if (period.label.isNotBlank()) {
                                    Text(
                                        text = period.label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(Modifier.height(4.dp))
                                }

                                Text(
                                    text = formatTimeRange(period.startTime, period.endTime),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Spacer(Modifier.height(6.dp))

                                val repeatText = period.repeatDays.split(",").filter { it.isNotBlank() }.joinToString(", ")
                                Text("Repeat: ${if (repeatText.isBlank()) "N/A" else repeatText}", style = MaterialTheme.typography.bodySmall)
                                Text("Mode: ${period.mode}", style = MaterialTheme.typography.bodySmall)

                                ScheduleCountdown(period)

                                if (isTodayIncluded(period.repeatDays) && hasScheduleEnded(period.endTime)) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "✅ Done for today",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }

                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    OutlinedButton(onClick = { onEditClick(period.id) }, modifier = Modifier.weight(1f)) {
                                        Text("Edit")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.deleteSilentPeriod(period)
                                            SilentModeScheduler.cancelScheduledAlarms(context, period.id)
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatTimeRange(startTime: String, endTime: String): String {
    val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return try {
        val start = inputFormat.parse(startTime)
        val end = inputFormat.parse(endTime)
        if (start != null && end != null) {
            "${outputFormat.format(start)} - ${outputFormat.format(end)}"
        } else {
            "$startTime - $endTime"
        }
    } catch (e: Exception) {
        "$startTime - $endTime"
    }
}


fun isTodayIncluded(repeatDays: String): Boolean {
    val today = SimpleDateFormat("EEE", Locale.getDefault()).format(Date())
    return repeatDays.split(",").map { it.trim() }.contains(today)
}

fun hasScheduleEnded(endTime: String): Boolean {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val end = format.parse(endTime)
        val now = Calendar.getInstance()
        val endCal = Calendar.getInstance().apply {
            if (end != null) {
                set(Calendar.HOUR_OF_DAY, end.hours)
                set(Calendar.MINUTE, end.minutes)
                set(Calendar.SECOND, 0)
            }
        }
        now.after(endCal)
    } catch (e: Exception) {
        false
    }
}
