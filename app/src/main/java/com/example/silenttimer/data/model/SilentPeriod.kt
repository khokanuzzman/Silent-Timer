package com.example.silenttimer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class SilentPeriod(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: String,
    val endTime: String,
    val repeatDays: String,
    val mode: String,
    val isActive: Boolean = true,
    val label: String = "" // <-- Add this line
)
