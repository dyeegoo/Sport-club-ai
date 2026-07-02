package com.sportclubai.presentation.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Attendance
import com.sportclubai.domain.usecase.GetAttendanceSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class AttendanceSummaryState {
    object Loading : AttendanceSummaryState()
    data class Success(
        val attendances: List<Attendance>,
        val totalPresent: Int,
        val totalAbsent: Int,
        val totalLate: Int
    ) : AttendanceSummaryState()
    data class Error(val message: String) : AttendanceSummaryState()
}

@HiltViewModel
class AttendanceSummaryViewModel @Inject constructor(
    private val getAttendanceSummaryUseCase: GetAttendanceSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceSummaryState>(AttendanceSummaryState.Loading)
    val uiState = _uiState.asStateFlow()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _startDate = MutableStateFlow(getStartOfMonth())
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow(sdf.format(Date()))
    val endDate = _endDate.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = AttendanceSummaryState.Loading
            try {
                getAttendanceSummaryUseCase(_startDate.value, _endDate.value)
                    .catch { e ->
                        _uiState.value = AttendanceSummaryState.Error(e.message ?: "Failed to load summary")
                    }
                    .collect { attendances ->
                        val present = attendances.count { it.status == "PRESENT" }
                        val absent = attendances.count { it.status == "ABSENT" }
                        val late = attendances.count { it.status == "LATE" }
                        _uiState.value = AttendanceSummaryState.Success(attendances, present, absent, late)
                    }
            } catch (e: Exception) {
                _uiState.value = AttendanceSummaryState.Error(e.message ?: "Failed to load summary")
            }
        }
    }

    fun setDateRange(start: String, end: String) {
        _startDate.value = start
        _endDate.value = end
        loadData()
    }

    private fun getStartOfMonth(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return sdf.format(calendar.time)
    }
}
