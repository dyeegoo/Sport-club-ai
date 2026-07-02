package com.sportclubai.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.*
import com.sportclubai.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReportsState {
    object Loading : ReportsState()
    data class Success(
        val overview: ClubOverviewStats,
        val financial: FinancialAnalytics,
        val coaches: List<CoachStats>
    ) : ReportsState()
    data class Error(val message: String) : ReportsState()
}

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getClubOverviewStatsUseCase: GetClubOverviewStatsUseCase,
    private val getFinancialAnalyticsUseCase: GetFinancialAnalyticsUseCase,
    private val getCoachStatsUseCase: GetCoachStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportsState>(ReportsState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = ReportsState.Loading
            try {
                val overviewFlow = getClubOverviewStatsUseCase()
                val financialFlow = getFinancialAnalyticsUseCase()
                val coachesFlow = getCoachStatsUseCase()

                combine(
                    overviewFlow,
                    financialFlow,
                    coachesFlow
                ) { overview, financial, coaches ->
                    ReportsState.Success(overview, financial, coaches)
                }.catch { e ->
                    _uiState.value = ReportsState.Error(e.message ?: "Failed to load reports")
                }.collect {
                    _uiState.value = it
                }
            } catch (e: Exception) {
                _uiState.value = ReportsState.Error(e.message ?: "Failed to load reports")
            }
        }
    }
}
