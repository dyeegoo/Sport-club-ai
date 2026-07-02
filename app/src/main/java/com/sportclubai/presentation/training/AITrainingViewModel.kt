package com.sportclubai.presentation.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.TrainingPlan
import com.sportclubai.domain.repository.TrainingRepository
import com.sportclubai.domain.usecase.GenerateAITrainingPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AITrainingState {
    object Loading : AITrainingState()
    data class Success(val plans: List<TrainingPlan>) : AITrainingState()
    data class Error(val message: String) : AITrainingState()
}

@HiltViewModel
class AITrainingViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val generateAITrainingPlanUseCase: GenerateAITrainingPlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AITrainingState>(AITrainingState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadPlansForStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = AITrainingState.Loading
            trainingRepository.getTrainingPlansForStudent(studentId)
                .catch { e -> _uiState.value = AITrainingState.Error(e.message ?: "Failed to load plans") }
                .collect { plans ->
                    _uiState.value = AITrainingState.Success(plans)
                }
        }
    }

    fun generatePlan(
        studentId: String,
        sportType: String,
        currentBelt: String,
        age: Int,
        attendancePercentage: Double,
        weakSkills: List<String>,
        strongSkills: List<String>,
        weeklyAvailabilityDays: Int,
        targetExamDate: Long?,
        coachNotes: String
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.value = AITrainingState.Loading
            try {
                val plan = generateAITrainingPlanUseCase(
                    studentId = studentId,
                    sportType = sportType,
                    currentBelt = currentBelt,
                    age = age,
                    attendancePercentage = attendancePercentage,
                    weakSkills = weakSkills,
                    strongSkills = strongSkills,
                    weeklyAvailabilityDays = weeklyAvailabilityDays,
                    targetExamDate = targetExamDate,
                    coachNotes = coachNotes
                )
                trainingRepository.saveTrainingPlan(plan)
                // The flow will automatically update the list if loadPlansForStudent was called
            } catch (e: Exception) {
                _uiState.value = AITrainingState.Error(e.message ?: "Failed to generate plan")
            }
        }
    }
}
