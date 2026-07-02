package com.sportclubai.presentation.coaches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.usecase.AddCoachUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddCoachState {
    object Idle : AddCoachState()
    object Loading : AddCoachState()
    object Success : AddCoachState()
    data class Error(val message: String) : AddCoachState()
}

@HiltViewModel
class AddCoachViewModel @Inject constructor(
    private val addCoachUseCase: AddCoachUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddCoachState>(AddCoachState.Idle)
    val uiState = _uiState.asStateFlow()

    val fullName = MutableStateFlow("")
    val phoneNumber = MutableStateFlow("")
    val email = MutableStateFlow("")
    val specialization = MutableStateFlow("")
    val status = MutableStateFlow("ACTIVE")
    val notes = MutableStateFlow("")

    fun saveCoach() {
        if (fullName.value.isBlank()) {
            _uiState.value = AddCoachState.Error("Full Name is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddCoachState.Loading
            try {
                val coach = Coach(
                    fullName = fullName.value,
                    phoneNumber = phoneNumber.value,
                    email = email.value,
                    specialization = specialization.value,
                    status = status.value,
                    notes = notes.value
                )
                addCoachUseCase(coach)
                _uiState.value = AddCoachState.Success
            } catch (e: Exception) {
                _uiState.value = AddCoachState.Error(e.message ?: "Failed to add coach")
            }
        }
    }
}
