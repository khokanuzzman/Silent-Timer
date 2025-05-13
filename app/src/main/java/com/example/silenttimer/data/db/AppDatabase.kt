package com.example.silenttimer.data.db

import com.example.silenttimer.data.model.SilentPeriod
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SilentPeriod::class], version = 2)
@TypeConverters(RepeatDayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun silentPeriodDao(): SilentPeriodDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "silent_timer_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
        }
    }
}
