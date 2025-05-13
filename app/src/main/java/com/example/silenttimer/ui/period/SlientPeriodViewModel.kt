package com.example.silenttimer.ui.period

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.silenttimer.data.model.SilentPeriod
import com.example.silenttimer.data.repository.SilentPeriodRepository
import kotlinx.coroutines.launch

class SilentPeriodViewModel(private val repository: SilentPeriodRepository) : ViewModel() {
    val silentPeriods = repository.allPeriods

    fun addSilentPeriod(period: SilentPeriod) {
        viewModelScope.launch { repository.insert(period) }
    }

    fun updateSilentPeriod(period: SilentPeriod) {
        viewModelScope.launch { repository.update(period) }
    }

    fun deleteSilentPeriod(period: SilentPeriod) {
        viewModelScope.launch { repository.delete(period) }
    }
}

class SilentPeriodViewModelFactory(private val repository: SilentPeriodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SilentPeriodViewModel(repository) as T
    }
}
