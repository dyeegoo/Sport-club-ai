package com.sportclubai.presentation.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.SportClass
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MyScheduleState {
    object Loading : MyScheduleState()
    data class Success(val classes: List<SportClass>) : MyScheduleState()
    data class Error(val message: String) : MyScheduleState()
}

@HiltViewModel
class MyScheduleViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyScheduleState>(MyScheduleState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = MyScheduleState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                classRepository.getClassesByClub(user.clubId)
                    .map { classes -> 
                        // Filter classes where this student is enrolled
                        classes.filter { it.enrolledStudents.contains(uid) }
                    }
                    .catch { e -> _uiState.value = MyScheduleState.Error(e.message ?: "Error") }
                    .collect { list ->
                        _uiState.value = MyScheduleState.Success(list)
                    }
            } catch (e: Exception) {
                _uiState.value = MyScheduleState.Error(e.message ?: "Failed to load data")
            }
        }
    }
}
