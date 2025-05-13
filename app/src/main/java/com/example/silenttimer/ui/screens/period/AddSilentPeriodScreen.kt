package com.example.silenttimer.ui.screens.period

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.silenttimer.data.model.SilentPeriod
import com.example.silenttimer.ui.components.RepeatDaySelector
import com.example.silenttimer.util.DndPermissionHelper
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSilentPeriodScreen(
    isEditMode: Boolean,
    period: SilentPeriod?,
    onSave: (SilentPeriod) -> Unit
) {
    val context = LocalContext.current

    var startTime by remember { mutableStateOf(period?.startTime ?: "") }
    var endTime by remember { mutableStateOf(period?.endTime ?: "") }
    var repeatDays by remember { mutableStateOf(period?.repeatDays?.split(",") ?: emptyList()) }
    var mode by remember { mutableStateOf(period?.mode ?: "SILENT") }
    var label by remember { mutableStateOf(period?.label ?: "") }
    val hasDndPermission = remember { DndPermissionHelper.isDndAccessGranted(context) }

    LaunchedEffect(Unit) {
        DndPermissionHelper.requestDndPermissionIfNeeded(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Schedule" else "Create Schedule",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (context is ComponentActivity) context.finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TimePickerRow("Start Time", startTime) {
                showTimePicker(context) { startTime = it }
            }

            TimePickerRow("End Time", endTime) {
                showTimePicker(context) { endTime = it }
            }

            Column {
                Text("Repeat Days", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                RepeatDaySelector(selectedDays = repeatDays) { repeatDays = it }
                AnimatedVisibility(visible = repeatDays.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    Text("Selected: ${repeatDays.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                }
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label (e.g. 📚 Class)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column {
                Text("Notification Mode", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                listOf("SILENT", "VIBRATE").forEach { item ->
                    val icon = if (item == "SILENT") Icons.Default.Notifications else Icons.Default.Notifications
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = mode == item, onClick = { mode = item })
                        Spacer(Modifier.width(8.dp))
                        Icon(icon, contentDescription = item)
                        Spacer(Modifier.width(6.dp))
                        Text(item.capitalize(), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            if (!hasDndPermission) {
                Text(
                    "⚠️ Do Not Disturb access not granted. Please enable it in settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
                        val updatedPeriod = SilentPeriod(
                            id = period?.id ?: 0,
                            startTime = startTime,
                            endTime = endTime,
                            repeatDays = repeatDays.joinToString(","),
                            mode = mode,
                            label = label
                        )
                        onSave(updatedPeriod)
                        Toast.makeText(context, "Schedule Saved", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Start and End time required", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isEditMode) "Update Schedule" else "Save Schedule")
            }
        }
    }
}

@Composable
fun TimePickerRow(label: String, value: String, onPick: () -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        FilledTonalButton(
            onClick = onPick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DateRange, contentDescription = label)
            Spacer(Modifier.width(8.dp))
            Text(if (value.isEmpty()) "Pick $label" else value)
        }
    }
}

fun showTimePicker(context: android.content.Context, onTimePicked: (String) -> Unit) {
    val cal = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute ->
            val formatted = String.format("%02d:%02d", hour, minute)
            onTimePicked(formatted)
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        true
    ).show()
}
