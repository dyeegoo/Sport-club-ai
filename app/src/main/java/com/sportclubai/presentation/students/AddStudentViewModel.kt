package com.sportclubai.presentation.students

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.usecase.AddStudentUseCase
import com.sportclubai.domain.usecase.CheckUsageLimitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddStudentState {
    object Idle : AddStudentState()
    object Loading : AddStudentState()
    object Success : AddStudentState()
    data class Error(val message: String) : AddStudentState()
    object LimitReached : AddStudentState()
}

@HiltViewModel
class AddStudentViewModel @Inject constructor(
    private val addStudentUseCase: AddStudentUseCase,
    private val checkUsageLimitUseCase: CheckUsageLimitUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    val fullName = MutableStateFlow("")
    val phoneNumber = MutableStateFlow("")
    val parentName = MutableStateFlow("")
    val parentPhone = MutableStateFlow("")
    val birthDate = MutableStateFlow("")
    val gender = MutableStateFlow("Male")
    val height = MutableStateFlow("")
    val weight = MutableStateFlow("")
    val beltLevel = MutableStateFlow("White")
    
    val profileImageUri = MutableStateFlow<Uri?>(null)

    private val _uiState = MutableStateFlow<AddStudentState>(AddStudentState.Idle)
    val uiState = _uiState.asStateFlow()

    fun addStudent() {
        if (fullName.value.isBlank()) {
            _uiState.value = AddStudentState.Error("Full Name is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddStudentState.Loading
            try {
                val uid = authRepository.getCurrentUserId()
                if (uid != null) {
                    val user = authRepository.getUser(uid)
                    if (user != null) {
                        val canAdd = checkUsageLimitUseCase.canAddStudent(user.clubId)
                        if (!canAdd) {
                            _uiState.value = AddStudentState.LimitReached
                            return@launch
                        }
                    }
                }

                val student = Student(
                    fullName = fullName.value,
                    phoneNumber = phoneNumber.value,
                    parentName = parentName.value,
                    parentPhone = parentPhone.value,
                    birthDate = birthDate.value,
                    gender = gender.value,
                    height = height.value.toFloatOrNull() ?: 0f,
                    weight = weight.value.toFloatOrNull() ?: 0f,
                    beltLevel = beltLevel.value,
                    membershipStatus = "Active"
                )
                addStudentUseCase(student, profileImageUri.value)
                _uiState.value = AddStudentState.Success
            } catch (e: Exception) {
                _uiState.value = AddStudentState.Error(e.message ?: "Failed to add student")
            }
        }
    }
}
