package com.sportclubai.presentation.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Subscription
import com.sportclubai.domain.model.UsageStats
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SubscriptionDashboardState {
    object Loading : SubscriptionDashboardState()
    data class Success(val subscription: Subscription, val usage: UsageStats) : SubscriptionDashboardState()
    data class Error(val message: String) : SubscriptionDashboardState()
}

@HiltViewModel
class SubscriptionDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubscriptionDashboardState>(SubscriptionDashboardState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = SubscriptionDashboardState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                val sub = subscriptionRepository.getSubscription(user.clubId).firstOrNull() ?: Subscription(clubId = user.clubId)
                val usage = subscriptionRepository.getUsageStats(user.clubId).firstOrNull() ?: UsageStats(clubId = user.clubId)
                
                _uiState.value = SubscriptionDashboardState.Success(sub, usage)
            } catch (e: Exception) {
                _uiState.value = SubscriptionDashboardState.Error(e.message ?: "Failed to load subscription data")
            }
        }
    }
}
