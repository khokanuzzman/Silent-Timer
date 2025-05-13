package com.example.silenttimer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepeatDaySelector(
    selectedDays: List<String>,
    onChange: (List<String>) -> Unit
) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        daysOfWeek.forEach { day ->
            val isSelected = selectedDays.contains(day)
            FilterChip(
                selected = isSelected,
                onClick = {
                    val updated = if (isSelected) selectedDays - day else selectedDays + day
                    onChange(updated)
                },
                label = { Text(day) }
            )
        }
    }
}
