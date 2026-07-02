package com.sportclubai.presentation.coaches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.usecase.GetCoachesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CoachesState {
    object Loading : CoachesState()
    data class Success(val coaches: List<Coach>) : CoachesState()
    data class Error(val message: String) : CoachesState()
}

@HiltViewModel
class CoachesViewModel @Inject constructor(
    private val getCoachesUseCase: GetCoachesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CoachesState>(CoachesState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _allCoaches = MutableStateFlow<List<Coach>>(emptyList())

    init {
        loadCoaches()
    }

    fun loadCoaches() {
        viewModelScope.launch {
            _uiState.value = CoachesState.Loading
            try {
                getCoachesUseCase()
                    .catch { e ->
                        _uiState.value = CoachesState.Error(e.message ?: "Failed to load coaches")
                    }
                    .collect { coaches ->
                        _allCoaches.value = coaches
                        filterCoaches()
                    }
            } catch (e: Exception) {
                _uiState.value = CoachesState.Error(e.message ?: "Failed to load coaches")
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterCoaches()
    }

    private fun filterCoaches() {
        val query = _searchQuery.value.lowercase()
        val filtered = if (query.isEmpty()) {
            _allCoaches.value
        } else {
            _allCoaches.value.filter {
                it.fullName.lowercase().contains(query) ||
                it.specialization.lowercase().contains(query)
            }
        }
        _uiState.value = CoachesState.Success(filtered)
    }
}
