package com.sportclubai.presentation.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.usecase.GetStudentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StudentsState {
    object Loading : StudentsState()
    data class Success(val students: List<Student>) : StudentsState()
    data class Error(val message: String) : StudentsState()
}

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val getStudentsUseCase: GetStudentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentsState>(StudentsState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    private val _beltFilter = MutableStateFlow("All")
    val beltFilter = _beltFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter = _statusFilter.asStateFlow()

    private var allStudents: List<Student> = emptyList()

    init {
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = StudentsState.Loading
            try {
                getStudentsUseCase()
                    .catch { e -> _uiState.value = StudentsState.Error(e.message ?: "Failed to load students") }
                    .collect { students ->
                        allStudents = students
                        applyFilters()
                    }
            } catch (e: Exception) {
                _uiState.value = StudentsState.Error(e.message ?: "Failed to load students")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }
    
    fun updateBeltFilter(belt: String) {
        _beltFilter.value = belt
        applyFilters()
    }

    fun updateStatusFilter(status: String) {
        _statusFilter.value = status
        applyFilters()
    }

    private fun applyFilters() {
        val query = _searchQuery.value.lowercase()
        val belt = _beltFilter.value
        val status = _statusFilter.value

        val filtered = allStudents.filter { student ->
            val matchesName = student.fullName.lowercase().contains(query)
            val matchesBelt = if (belt == "All") true else student.beltLevel == belt
            val matchesStatus = if (status == "All") true else student.membershipStatus == status
            
            matchesName && matchesBelt && matchesStatus
        }
        
        _uiState.value = StudentsState.Success(filtered)
    }
}
