package com.example.silenttimer.data.db

import androidx.room.TypeConverter

class RepeatDayConverter {
    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun toList(data: String): List<String> =
        if (data.isBlank()) emptyList() else data.split(",")
}
