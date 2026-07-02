package com.sportclubai.presentation.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Exam
import com.sportclubai.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExamDetailState {
    object Loading : ExamDetailState()
    data class Success(val exam: Exam) : ExamDetailState()
    data class Error(val message: String) : ExamDetailState()
}

@HiltViewModel
class ExamDetailViewModel @Inject constructor(
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExamDetailState>(ExamDetailState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadExam(examId: String) {
        viewModelScope.launch {
            _uiState.value = ExamDetailState.Loading
            try {
                val exam = examRepository.getExamById(examId)
                if (exam != null) {
                    _uiState.value = ExamDetailState.Success(exam)
                } else {
                    _uiState.value = ExamDetailState.Error("Exam not found")
                }
            } catch (e: Exception) {
                _uiState.value = ExamDetailState.Error(e.message ?: "Failed to load exam")
            }
        }
    }
}
