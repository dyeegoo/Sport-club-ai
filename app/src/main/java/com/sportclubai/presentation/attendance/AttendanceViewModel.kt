package com.sportclubai.presentation.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Attendance
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.usecase.GetAttendanceByDateUseCase
import com.sportclubai.domain.usecase.GetStudentsUseCase
import com.sportclubai.domain.usecase.MarkBulkAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class AttendanceItem(
    val student: Student,
    val attendance: Attendance?
)

sealed class AttendanceState {
    object Loading : AttendanceState()
    data class Success(val items: List<AttendanceItem>) : AttendanceState()
    data class Error(val message: String) : AttendanceState()
}

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val getAttendanceByDateUseCase: GetAttendanceByDateUseCase,
    private val markBulkAttendanceUseCase: MarkBulkAttendanceUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate = _selectedDate.asStateFlow()

    private val _uiState = MutableStateFlow<AttendanceState>(AttendanceState.Loading)
    val uiState = _uiState.asStateFlow()

    private var currentStudents: List<Student> = emptyList()
    private var currentAttendances: List<Attendance> = emptyList()

    init {
        loadData()
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = AttendanceState.Loading
            try {
                val date = _selectedDate.value
                val studentsFlow = getStudentsUseCase()
                val attendanceFlow = getAttendanceByDateUseCase(date)

                studentsFlow.combine(attendanceFlow) { students, attendances ->
                    currentStudents = students
                    currentAttendances = attendances
                    
                    students.map { student ->
                        val attendance = attendances.find { it.studentId == student.studentId }
                        AttendanceItem(student, attendance)
                    }.sortedBy { it.student.fullName }
                }.catch { e ->
                    _uiState.value = AttendanceState.Error(e.message ?: "Failed to load data")
                }.collect { items ->
                    _uiState.value = AttendanceState.Success(items)
                }
            } catch (e: Exception) {
                _uiState.value = AttendanceState.Error(e.message ?: "Failed to load data")
            }
        }
    }

    fun markAttendance(studentId: String, status: String) {
        val date = _selectedDate.value
        val existingAttendance = currentAttendances.find { it.studentId == studentId }
        
        val newAttendance = existingAttendance?.copy(status = status) ?: Attendance(
            studentId = studentId,
            date = date,
            status = status,
            checkInTime = System.currentTimeMillis()
        )
        
        viewModelScope.launch {
            try {
                markBulkAttendanceUseCase(listOf(newAttendance))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun markAll(status: String) {
        val date = _selectedDate.value
        val newAttendances = currentStudents.map { student ->
            val existingAttendance = currentAttendances.find { it.studentId == student.studentId }
            existingAttendance?.copy(status = status) ?: Attendance(
                studentId = student.studentId,
                date = date,
                status = status,
                checkInTime = System.currentTimeMillis()
            )
        }

        viewModelScope.launch {
            try {
                markBulkAttendanceUseCase(newAttendances)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
