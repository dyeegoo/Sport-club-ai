package com.sportclubai.presentation.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Exam
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExamDashboardState {
    object Loading : ExamDashboardState()
    data class Success(val exams: List<Exam>) : ExamDashboardState()
    data class Error(val message: String) : ExamDashboardState()
}

@HiltViewModel
class ExamDashboardViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExamDashboardState>(ExamDashboardState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadExams()
    }

    fun loadExams() {
        viewModelScope.launch {
            _uiState.value = ExamDashboardState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                examRepository.getExamsForClub(user.clubId)
                    .catch { e -> _uiState.value = ExamDashboardState.Error(e.message ?: "Failed to load") }
                    .collect { exams ->
                        _uiState.value = ExamDashboardState.Success(exams)
                    }
            } catch (e: Exception) {
                _uiState.value = ExamDashboardState.Error(e.message ?: "Error loading exams")
            }
        }
    }
}
