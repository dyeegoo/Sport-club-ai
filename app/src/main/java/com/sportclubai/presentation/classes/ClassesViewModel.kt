package com.sportclubai.presentation.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.model.SportClass
import com.sportclubai.domain.usecase.GetClassesUseCase
import com.sportclubai.domain.usecase.GetCoachesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClassesState {
    object Loading : ClassesState()
    data class Success(val classes: List<SportClass>, val coachesMap: Map<String, Coach>) : ClassesState()
    data class Error(val message: String) : ClassesState()
}

@HiltViewModel
class ClassesViewModel @Inject constructor(
    private val getClassesUseCase: GetClassesUseCase,
    private val getCoachesUseCase: GetCoachesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClassesState>(ClassesState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = ClassesState.Loading
            try {
                combine(
                    getClassesUseCase(),
                    getCoachesUseCase()
                ) { classes, coaches ->
                    val coachesMap = coaches.associateBy { it.coachId }
                    ClassesState.Success(classes, coachesMap)
                }.catch { e ->
                    _uiState.value = ClassesState.Error(e.message ?: "Failed to load classes")
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = ClassesState.Error(e.message ?: "Failed to load classes")
            }
        }
    }
}
