package com.sportclubai.presentation.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.model.SportClass
import com.sportclubai.domain.usecase.CreateClassUseCase
import com.sportclubai.domain.usecase.GetCoachesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateClassState {
    object Idle : CreateClassState()
    object Loading : CreateClassState()
    object Success : CreateClassState()
    data class Error(val message: String) : CreateClassState()
}

@HiltViewModel
class CreateClassViewModel @Inject constructor(
    private val createClassUseCase: CreateClassUseCase,
    private val getCoachesUseCase: GetCoachesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateClassState>(CreateClassState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _coaches = MutableStateFlow<List<Coach>>(emptyList())
    val coaches = _coaches.asStateFlow()

    val className = MutableStateFlow("")
    val selectedCoachId = MutableStateFlow("")
    val startTime = MutableStateFlow("")
    val endTime = MutableStateFlow("")
    val maxStudents = MutableStateFlow("")
    val location = MutableStateFlow("")
    val selectedDays = MutableStateFlow<Set<String>>(emptySet())

    init {
        loadCoaches()
    }

    private fun loadCoaches() {
        viewModelScope.launch {
            try {
                getCoachesUseCase()
                    .catch { /* ignore or log */ }
                    .collect { coachList ->
                        _coaches.value = coachList
                    }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun toggleDay(day: String) {
        val current = selectedDays.value.toMutableSet()
        if (current.contains(day)) {
            current.remove(day)
        } else {
            current.add(day)
        }
        selectedDays.value = current
    }

    fun saveClass() {
        if (className.value.isBlank()) {
            _uiState.value = CreateClassState.Error("Class Name is required")
            return
        }

        val maxStudentsInt = maxStudents.value.toIntOrNull()
        if (maxStudentsInt == null || maxStudentsInt <= 0) {
            _uiState.value = CreateClassState.Error("Please enter a valid max students number")
            return
        }

        viewModelScope.launch {
            _uiState.value = CreateClassState.Loading
            try {
                val sportClass = SportClass(
                    className = className.value,
                    coachId = selectedCoachId.value,
                    scheduleDays = selectedDays.value.toList(),
                    startTime = startTime.value,
                    endTime = endTime.value,
                    maxStudents = maxStudentsInt,
                    location = location.value,
                    status = "ACTIVE"
                )
                createClassUseCase(sportClass)
                _uiState.value = CreateClassState.Success
            } catch (e: Exception) {
                _uiState.value = CreateClassState.Error(e.message ?: "Failed to create class")
            }
        }
    }
}
