package com.example.silenttimer.ui.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
//@TypeConverters(RepeatDayConverter::class)
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: String, // e.g., "13:00"
    val endTime: String,   // e.g., "14:00"
    val repeatDays: List<String>, // ["Mon", "Wed"]
    val mode: String,      // "SILENT" or "VIBRATE"
    val isActive: Boolean = true
)
