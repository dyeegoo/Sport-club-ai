package com.sportclubai.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.SearchResult
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalSearchViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    val results = _results.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadRecentSearches()
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun search() {
        val currentQuery = query.value
        if (currentQuery.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch
                val user = authRepository.getUser(uid) ?: return@launch
                
                searchRepository.saveRecentSearch(user.clubId, currentQuery)
                val searchResults = searchRepository.search(user.clubId, currentQuery)
                _results.value = searchResults
                loadRecentSearches()
            } catch (e: Exception) {
                // handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch
                val user = authRepository.getUser(uid) ?: return@launch
                _recentSearches.value = searchRepository.getRecentSearches(user.clubId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
