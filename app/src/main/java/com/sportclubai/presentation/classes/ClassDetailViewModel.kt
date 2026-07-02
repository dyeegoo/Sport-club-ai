package com.sportclubai.presentation.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.model.SportClass
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.usecase.AddStudentToClassUseCase
import com.sportclubai.domain.usecase.GetClassByIdUseCase
import com.sportclubai.domain.usecase.GetCoachByIdUseCase
import com.sportclubai.domain.usecase.GetStudentByIdUseCase
import com.sportclubai.domain.usecase.RemoveStudentFromClassUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClassDetailState {
    object Loading : ClassDetailState()
    data class Success(
        val sportClass: SportClass,
        val coach: Coach?,
        val students: List<Student>
    ) : ClassDetailState()
    data class Error(val message: String) : ClassDetailState()
}

@HiltViewModel
class ClassDetailViewModel @Inject constructor(
    private val getClassByIdUseCase: GetClassByIdUseCase,
    private val getCoachByIdUseCase: GetCoachByIdUseCase,
    private val getStudentByIdUseCase: GetStudentByIdUseCase,
    private val addStudentToClassUseCase: AddStudentToClassUseCase,
    private val removeStudentFromClassUseCase: RemoveStudentFromClassUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClassDetailState>(ClassDetailState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadClass(classId: String) {
        viewModelScope.launch {
            _uiState.value = ClassDetailState.Loading
            try {
                val sportClass = getClassByIdUseCase(classId)
                if (sportClass != null) {
                    val coach = if (sportClass.coachId.isNotEmpty()) {
                        getCoachByIdUseCase(sportClass.coachId)
                    } else null
                    
                    val students = sportClass.assignedStudents.mapNotNull { studentId ->
                        getStudentByIdUseCase(studentId)
                    }
                    
                    _uiState.value = ClassDetailState.Success(sportClass, coach, students)
                } else {
                    _uiState.value = ClassDetailState.Error("Class not found")
                }
            } catch (e: Exception) {
                _uiState.value = ClassDetailState.Error(e.message ?: "Failed to load class details")
            }
        }
    }

    fun removeStudent(classId: String, studentId: String) {
        viewModelScope.launch {
            try {
                removeStudentFromClassUseCase(classId, studentId)
                loadClass(classId) // Reload data
            } catch (e: Exception) {
                _uiState.value = ClassDetailState.Error(e.message ?: "Failed to remove student")
            }
        }
    }
}
