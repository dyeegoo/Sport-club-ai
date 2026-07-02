package com.sportclubai.presentation.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.TrainingPlan
import com.sportclubai.domain.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TrainingPlanDetailState {
    object Loading : TrainingPlanDetailState()
    data class Success(val plan: TrainingPlan) : TrainingPlanDetailState()
    data class Error(val message: String) : TrainingPlanDetailState()
}

@HiltViewModel
class TrainingPlanDetailViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrainingPlanDetailState>(TrainingPlanDetailState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadPlan(planId: String) {
        viewModelScope.launch {
            _uiState.value = TrainingPlanDetailState.Loading
            try {
                val plan = trainingRepository.getTrainingPlan(planId)
                if (plan != null) {
                    _uiState.value = TrainingPlanDetailState.Success(plan)
                } else {
                    _uiState.value = TrainingPlanDetailState.Error("Plan not found")
                }
            } catch (e: Exception) {
                _uiState.value = TrainingPlanDetailState.Error(e.message ?: "Error loading plan")
            }
        }
    }

    fun toggleExercise(planId: String, weekNumber: Int, dayOfWeek: String, exerciseId: String, completed: Boolean) {
        viewModelScope.launch {
            try {
                trainingRepository.markExerciseCompleted(planId, weekNumber, dayOfWeek, exerciseId, completed)
                // Reload to get updated state
                loadPlan(planId)
            } catch (e: Exception) {
                // Ignore for now or handle
            }
        }
    }
}
