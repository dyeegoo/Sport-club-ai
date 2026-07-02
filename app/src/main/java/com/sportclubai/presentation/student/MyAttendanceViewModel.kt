package com.sportclubai.presentation.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Attendance
import com.sportclubai.domain.repository.AttendanceRepository
import com.sportclubai.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MyAttendanceState {
    object Loading : MyAttendanceState()
    data class Success(val attendances: List<Attendance>) : MyAttendanceState()
    data class Error(val message: String) : MyAttendanceState()
}

@HiltViewModel
class MyAttendanceViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyAttendanceState>(MyAttendanceState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = MyAttendanceState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                attendanceRepository.getStudentAttendanceHistory(user.clubId, uid)
                    .catch { e -> _uiState.value = MyAttendanceState.Error(e.message ?: "Error") }
                    .collect { list ->
                        _uiState.value = MyAttendanceState.Success(list.sortedByDescending { it.date })
                    }
            } catch (e: Exception) {
                _uiState.value = MyAttendanceState.Error(e.message ?: "Failed to load data")
            }
        }
    }
}
