package com.sportclubai.presentation.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MyProfileState {
    object Loading : MyProfileState()
    data class Success(val student: Student) : MyProfileState()
    data class Error(val message: String) : MyProfileState()
}

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyProfileState>(MyProfileState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = MyProfileState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                val student = studentRepository.getStudentById(user.clubId, uid) ?: throw Exception("Student profile not found")
                
                _uiState.value = MyProfileState.Success(student)
            } catch (e: Exception) {
                _uiState.value = MyProfileState.Error(e.message ?: "Failed to load profile")
            }
        }
    }
}
