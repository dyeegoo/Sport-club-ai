package com.sportclubai.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.StudentDashboardData
import com.sportclubai.domain.usecase.GetStudentDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StudentPanelState {
    object Loading : StudentPanelState()
    data class Success(val data: StudentDashboardData) : StudentPanelState()
    data class Error(val message: String) : StudentPanelState()
}

@HiltViewModel
class StudentPanelViewModel @Inject constructor(
    private val getStudentDashboardDataUseCase: GetStudentDashboardDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentPanelState>(StudentPanelState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = StudentPanelState.Loading
            try {
                getStudentDashboardDataUseCase()
                    .catch { e ->
                        _uiState.value = StudentPanelState.Error(e.message ?: "Failed to load dashboard")
                    }
                    .collect { data ->
                        _uiState.value = StudentPanelState.Success(data)
                    }
            } catch (e: Exception) {
                _uiState.value = StudentPanelState.Error(e.message ?: "Failed to load dashboard")
            }
        }
    }
}
