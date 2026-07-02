package com.sportclubai.presentation.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.ExamCriteria
import com.sportclubai.domain.model.ExamResult
import com.sportclubai.domain.model.ExamStudent
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExamEvaluationState {
    object Loading : ExamEvaluationState()
    data class Success(val student: ExamStudent, val criteria: List<ExamCriteria>) : ExamEvaluationState()
    data class Error(val message: String) : ExamEvaluationState()
}

@HiltViewModel
class ExamEvaluationViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExamEvaluationState>(ExamEvaluationState.Loading)
    val uiState = _uiState.asStateFlow()

    private var currentCriteria: List<ExamCriteria> = emptyList()

    fun loadEvaluationData(examId: String, studentId: String) {
        viewModelScope.launch {
            _uiState.value = ExamEvaluationState.Loading
            try {
                val exam = examRepository.getExamById(examId) ?: throw Exception("Exam not found")
                val student = exam.students.find { it.studentId == studentId } ?: ExamStudent(studentId = studentId, studentName = "Unknown")
                // In a real app we would ensure student exists in exam list, but if not we can add them or error
                currentCriteria = exam.criteria
                _uiState.value = ExamEvaluationState.Success(student, exam.criteria)
            } catch (e: Exception) {
                _uiState.value = ExamEvaluationState.Error(e.message ?: "Failed to load evaluation data")
            }
        }
    }

    fun saveEvaluation(examId: String, student: ExamStudent, results: List<ExamResult>) {
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUserId() ?: ""
                
                // Calculate if passed
                var allPassed = true
                var totalScore = 0.0
                results.forEach { res ->
                    totalScore += res.score
                    val crit = currentCriteria.find { it.name == res.criteriaName }
                    if (crit != null && res.score < crit.minPassScore) {
                        allPassed = false
                    }
                }
                
                val updatedStudent = student.copy(
                    results = results,
                    totalScore = totalScore,
                    passed = allPassed,
                    evaluatorId = uid
                )
                
                examRepository.updateStudentEvaluation(examId, updatedStudent)
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}
