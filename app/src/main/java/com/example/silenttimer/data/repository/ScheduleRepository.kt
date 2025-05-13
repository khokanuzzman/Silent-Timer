package com.example.silenttimer.data.repository

import com.example.silenttimer.data.db.SilentPeriodDao
import com.example.silenttimer.data.model.SilentPeriod

class SilentPeriodRepository(private val dao: SilentPeriodDao) {
    val allPeriods = dao.getAllSilentPeriods()

    suspend fun insert(period: SilentPeriod) = dao.insertSilentPeriod(period)
    suspend fun update(period: SilentPeriod) = dao.updateSilentPeriod(period)
    suspend fun delete(period: SilentPeriod) = dao.deleteSilentPeriod(period)
}
