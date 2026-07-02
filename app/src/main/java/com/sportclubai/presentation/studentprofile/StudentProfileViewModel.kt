package com.sportclubai.presentation.studentprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Attendance
import com.sportclubai.domain.model.Payment
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.usecase.GetStudentByIdUseCase
import com.sportclubai.domain.usecase.DeleteStudentUseCase
import com.sportclubai.domain.usecase.GetStudentAttendanceHistoryUseCase
import com.sportclubai.domain.usecase.GetPaymentsByStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StudentProfileState {
    object Loading : StudentProfileState()
    data class Success(
        val student: Student, 
        val attendances: List<Attendance> = emptyList(),
        val payments: List<Payment> = emptyList()
    ) : StudentProfileState()
    data class Error(val message: String) : StudentProfileState()
    object Deleted : StudentProfileState()
}

@HiltViewModel
class StudentProfileViewModel @Inject constructor(
    private val getStudentByIdUseCase: GetStudentByIdUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase,
    private val getStudentAttendanceHistoryUseCase: GetStudentAttendanceHistoryUseCase,
    private val getPaymentsByStudentUseCase: GetPaymentsByStudentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentProfileState>(StudentProfileState.Loading)
    val uiState = _uiState.asStateFlow()

    private var currentStudentId: String? = null

    fun loadStudent(studentId: String) {
        currentStudentId = studentId
        viewModelScope.launch {
            _uiState.value = StudentProfileState.Loading
            try {
                val student = getStudentByIdUseCase(studentId)
                if (student != null) {
                    val attendanceFlow = getStudentAttendanceHistoryUseCase(studentId).catch { emit(emptyList()) }
                    val paymentsFlow = getPaymentsByStudentUseCase(studentId).catch { emit(emptyList()) }
                    
                    attendanceFlow.combine(paymentsFlow) { attendances, payments ->
                        StudentProfileState.Success(student, attendances, payments)
                    }.collect { state ->
                        _uiState.value = state
                    }
                } else {
                    _uiState.value = StudentProfileState.Error("Student not found")
                }
            } catch (e: Exception) {
                _uiState.value = StudentProfileState.Error(e.message ?: "Failed to load student")
            }
        }
    }

    fun deleteStudent() {
        val studentId = currentStudentId ?: return
        viewModelScope.launch {
            try {
                deleteStudentUseCase(studentId)
                _uiState.value = StudentProfileState.Deleted
            } catch (e: Exception) {
                _uiState.value = StudentProfileState.Error(e.message ?: "Failed to delete student")
            }
        }
    }
}
