package com.sportclubai.presentation.coaches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.usecase.DeleteCoachUseCase
import com.sportclubai.domain.usecase.GetCoachByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CoachDetailState {
    object Loading : CoachDetailState()
    data class Success(val coach: Coach) : CoachDetailState()
    data class Error(val message: String) : CoachDetailState()
    object Deleted : CoachDetailState()
}

@HiltViewModel
class CoachDetailViewModel @Inject constructor(
    private val getCoachByIdUseCase: GetCoachByIdUseCase,
    private val deleteCoachUseCase: DeleteCoachUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CoachDetailState>(CoachDetailState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadCoach(coachId: String) {
        viewModelScope.launch {
            _uiState.value = CoachDetailState.Loading
            try {
                val coach = getCoachByIdUseCase(coachId)
                if (coach != null) {
                    _uiState.value = CoachDetailState.Success(coach)
                } else {
                    _uiState.value = CoachDetailState.Error("Coach not found")
                }
            } catch (e: Exception) {
                _uiState.value = CoachDetailState.Error(e.message ?: "Failed to load coach details")
            }
        }
    }

    fun deleteCoach(coachId: String) {
        viewModelScope.launch {
            try {
                deleteCoachUseCase(coachId)
                _uiState.value = CoachDetailState.Deleted
            } catch (e: Exception) {
                _uiState.value = CoachDetailState.Error(e.message ?: "Failed to delete coach")
            }
        }
    }
}
