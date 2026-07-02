package com.sportclubai.presentation.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.model.SportClass
import com.sportclubai.domain.usecase.GetClassByIdUseCase
import com.sportclubai.domain.usecase.GetCoachesUseCase
import com.sportclubai.domain.usecase.UpdateClassUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditClassState {
    object Idle : EditClassState()
    object Loading : EditClassState()
    object Success : EditClassState()
    data class Error(val message: String) : EditClassState()
}

@HiltViewModel
class EditClassViewModel @Inject constructor(
    private val getClassByIdUseCase: GetClassByIdUseCase,
    private val updateClassUseCase: UpdateClassUseCase,
    private val getCoachesUseCase: GetCoachesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditClassState>(EditClassState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _coaches = MutableStateFlow<List<Coach>>(emptyList())
    val coaches = _coaches.asStateFlow()

    private var originalClass: SportClass? = null

    val className = MutableStateFlow("")
    val selectedCoachId = MutableStateFlow("")
    val startTime = MutableStateFlow("")
    val endTime = MutableStateFlow("")
    val maxStudents = MutableStateFlow("")
    val location = MutableStateFlow("")
    val selectedDays = MutableStateFlow<Set<String>>(emptySet())
    val status = MutableStateFlow("ACTIVE")

    init {
        loadCoaches()
    }

    private fun loadCoaches() {
        viewModelScope.launch {
            try {
                getCoachesUseCase()
                    .catch { /* ignore */ }
                    .collect { coachList ->
                        _coaches.value = coachList
                    }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun loadClass(classId: String) {
        viewModelScope.launch {
            _uiState.value = EditClassState.Loading
            try {
                val sportClass = getClassByIdUseCase(classId)
                if (sportClass != null) {
                    originalClass = sportClass
                    className.value = sportClass.className
                    selectedCoachId.value = sportClass.coachId
                    startTime.value = sportClass.startTime
                    endTime.value = sportClass.endTime
                    maxStudents.value = sportClass.maxStudents.toString()
                    location.value = sportClass.location
                    selectedDays.value = sportClass.scheduleDays.toSet()
                    status.value = sportClass.status
                    _uiState.value = EditClassState.Idle
                } else {
                    _uiState.value = EditClassState.Error("Class not found")
                }
            } catch (e: Exception) {
                _uiState.value = EditClassState.Error(e.message ?: "Failed to load class")
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

    fun updateClass() {
        if (className.value.isBlank()) {
            _uiState.value = EditClassState.Error("Class Name is required")
            return
        }

        val maxStudentsInt = maxStudents.value.toIntOrNull()
        if (maxStudentsInt == null || maxStudentsInt <= 0) {
            _uiState.value = EditClassState.Error("Please enter a valid max students number")
            return
        }

        viewModelScope.launch {
            _uiState.value = EditClassState.Loading
            try {
                val updatedClass = originalClass?.copy(
                    className = className.value,
                    coachId = selectedCoachId.value,
                    scheduleDays = selectedDays.value.toList(),
                    startTime = startTime.value,
                    endTime = endTime.value,
                    maxStudents = maxStudentsInt,
                    location = location.value,
                    status = status.value
                ) ?: return@launch

                updateClassUseCase(updatedClass)
                _uiState.value = EditClassState.Success
            } catch (e: Exception) {
                _uiState.value = EditClassState.Error(e.message ?: "Failed to update class")
            }
        }
    }
}
