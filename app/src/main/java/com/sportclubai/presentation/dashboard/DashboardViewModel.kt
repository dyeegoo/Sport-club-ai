package com.sportclubai.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.DashboardData
import com.sportclubai.domain.usecase.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardData) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = DashboardState.Loading
            try {
                val data = getDashboardDataUseCase()
                _uiState.value = DashboardState.Success(data)
            } catch (e: Exception) {
                _uiState.value = DashboardState.Error(e.message ?: "Failed to load dashboard")
            }
        }
    }
}

