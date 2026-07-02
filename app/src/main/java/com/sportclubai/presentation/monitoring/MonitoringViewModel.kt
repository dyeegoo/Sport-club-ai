package com.sportclubai.presentation.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.MonitoringStats
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.MonitoringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MonitoringState {
    object Loading : MonitoringState()
    data class Success(val stats: MonitoringStats) : MonitoringState()
    data class Error(val message: String) : MonitoringState()
}

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val monitoringRepository: MonitoringRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MonitoringState>(MonitoringState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.value = MonitoringState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                monitoringRepository.getMonitoringStats(user.clubId)
                    .catch { e -> _uiState.value = MonitoringState.Error(e.message ?: "Failed to load monitoring stats") }
                    .collect { stats ->
                        _uiState.value = MonitoringState.Success(stats)
                    }
            } catch (e: Exception) {
                _uiState.value = MonitoringState.Error(e.message ?: "Failed to load monitoring stats")
            }
        }
    }
}
