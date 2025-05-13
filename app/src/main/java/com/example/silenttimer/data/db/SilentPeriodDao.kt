package com.example.silenttimer.data.db

import com.example.silenttimer.data.model.SilentPeriod
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SilentPeriodDao {

    @Query("SELECT * FROM schedules")
    fun getAllSilentPeriods(): LiveData<List<SilentPeriod>>

    @Query("SELECT * FROM schedules WHERE isActive = 1")
    suspend fun getAllNow(): List<SilentPeriod>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSilentPeriod(period: SilentPeriod)

    @Update
    suspend fun updateSilentPeriod(period: SilentPeriod)

    @Delete
    suspend fun deleteSilentPeriod(period: SilentPeriod)
}
