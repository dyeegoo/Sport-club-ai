package com.sportclubai.presentation.studentprofile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.usecase.GetStudentByIdUseCase
import com.sportclubai.domain.usecase.UpdateStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditStudentState {
    object Idle : EditStudentState()
    object Loading : EditStudentState()
    object Success : EditStudentState()
    data class Error(val message: String) : EditStudentState()
}

@HiltViewModel
class EditStudentViewModel @Inject constructor(
    private val getStudentByIdUseCase: GetStudentByIdUseCase,
    private val updateStudentUseCase: UpdateStudentUseCase
) : ViewModel() {

    private var currentStudent: Student? = null

    val fullName = MutableStateFlow("")
    val phoneNumber = MutableStateFlow("")
    val parentName = MutableStateFlow("")
    val parentPhone = MutableStateFlow("")
    val birthDate = MutableStateFlow("")
    val gender = MutableStateFlow("")
    val height = MutableStateFlow("")
    val weight = MutableStateFlow("")
    val beltLevel = MutableStateFlow("")
    val membershipStatus = MutableStateFlow("")
    val notes = MutableStateFlow("")
    
    val profileImageUri = MutableStateFlow<Uri?>(null)

    private val _uiState = MutableStateFlow<EditStudentState>(EditStudentState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = EditStudentState.Loading
            try {
                val student = getStudentByIdUseCase(studentId)
                if (student != null) {
                    currentStudent = student
                    fullName.value = student.fullName
                    phoneNumber.value = student.phoneNumber
                    parentName.value = student.parentName
                    parentPhone.value = student.parentPhone
                    birthDate.value = student.birthDate
                    gender.value = student.gender
                    height.value = student.height.toString()
                    weight.value = student.weight.toString()
                    beltLevel.value = student.beltLevel
                    membershipStatus.value = student.membershipStatus
                    notes.value = student.notes
                    
                    _uiState.value = EditStudentState.Idle
                } else {
                    _uiState.value = EditStudentState.Error("Student not found")
                }
            } catch (e: Exception) {
                _uiState.value = EditStudentState.Error(e.message ?: "Failed to load student")
            }
        }
    }

    fun updateStudent() {
        val studentId = currentStudent?.studentId ?: return
        val clubId = currentStudent?.clubId ?: return
        val registrationDate = currentStudent?.registrationDate ?: System.currentTimeMillis()
        val profileImage = currentStudent?.profileImage ?: ""
        val attendanceCount = currentStudent?.attendanceCount ?: 0
        val absenceCount = currentStudent?.absenceCount ?: 0

        viewModelScope.launch {
            _uiState.value = EditStudentState.Loading
            try {
                val student = Student(
                    studentId = studentId,
                    clubId = clubId,
                    fullName = fullName.value,
                    profileImage = profileImage,
                    phoneNumber = phoneNumber.value,
                    parentName = parentName.value,
                    parentPhone = parentPhone.value,
                    birthDate = birthDate.value,
                    gender = gender.value,
                    height = height.value.toFloatOrNull() ?: 0f,
                    weight = weight.value.toFloatOrNull() ?: 0f,
                    beltLevel = beltLevel.value,
                    registrationDate = registrationDate,
                    membershipStatus = membershipStatus.value,
                    notes = notes.value,
                    attendanceCount = attendanceCount,
                    absenceCount = absenceCount
                )
                updateStudentUseCase(student, profileImageUri.value)
                _uiState.value = EditStudentState.Success
            } catch (e: Exception) {
                _uiState.value = EditStudentState.Error(e.message ?: "Failed to update student")
            }
        }
    }
}
