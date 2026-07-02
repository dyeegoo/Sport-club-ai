package com.sportclubai.presentation.coaches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.usecase.GetCoachByIdUseCase
import com.sportclubai.domain.usecase.UpdateCoachUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditCoachState {
    object Idle : EditCoachState()
    object Loading : EditCoachState()
    object Success : EditCoachState()
    data class Error(val message: String) : EditCoachState()
}

@HiltViewModel
class EditCoachViewModel @Inject constructor(
    private val getCoachByIdUseCase: GetCoachByIdUseCase,
    private val updateCoachUseCase: UpdateCoachUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditCoachState>(EditCoachState.Idle)
    val uiState = _uiState.asStateFlow()

    private var currentCoachId: String = ""
    private var originalCoach: Coach? = null

    val fullName = MutableStateFlow("")
    val phoneNumber = MutableStateFlow("")
    val email = MutableStateFlow("")
    val specialization = MutableStateFlow("")
    val status = MutableStateFlow("ACTIVE")
    val notes = MutableStateFlow("")

    fun loadCoach(coachId: String) {
        currentCoachId = coachId
        viewModelScope.launch {
            _uiState.value = EditCoachState.Loading
            try {
                val coach = getCoachByIdUseCase(coachId)
                if (coach != null) {
                    originalCoach = coach
                    fullName.value = coach.fullName
                    phoneNumber.value = coach.phoneNumber
                    email.value = coach.email
                    specialization.value = coach.specialization
                    status.value = coach.status
                    notes.value = coach.notes
                    _uiState.value = EditCoachState.Idle
                } else {
                    _uiState.value = EditCoachState.Error("Coach not found")
                }
            } catch (e: Exception) {
                _uiState.value = EditCoachState.Error(e.message ?: "Failed to load coach")
            }
        }
    }

    fun updateCoach() {
        if (fullName.value.isBlank()) {
            _uiState.value = EditCoachState.Error("Full Name is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = EditCoachState.Loading
            try {
                val updatedCoach = originalCoach?.copy(
                    fullName = fullName.value,
                    phoneNumber = phoneNumber.value,
                    email = email.value,
                    specialization = specialization.value,
                    status = status.value,
                    notes = notes.value
                ) ?: return@launch

                updateCoachUseCase(updatedCoach)
                _uiState.value = EditCoachState.Success
            } catch (e: Exception) {
                _uiState.value = EditCoachState.Error(e.message ?: "Failed to update coach")
            }
        }
    }
}
