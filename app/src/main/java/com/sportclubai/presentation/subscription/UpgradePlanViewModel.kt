package com.sportclubai.presentation.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.SubscriptionPlan
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UpgradeStatus {
    object Idle : UpgradeStatus()
    object Loading : UpgradeStatus()
    object Success : UpgradeStatus()
    data class Error(val message: String) : UpgradeStatus()
}

@HiltViewModel
class UpgradePlanViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _upgradeStatus = MutableStateFlow<UpgradeStatus>(UpgradeStatus.Idle)
    val upgradeStatus = _upgradeStatus.asStateFlow()

    fun upgradePlan(plan: SubscriptionPlan) {
        viewModelScope.launch {
            _upgradeStatus.value = UpgradeStatus.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                // In a real flow, we would call billingRepository to get a checkout URL
                // then redirect the user. For this phase, we mock the upgrade directly.
                subscriptionRepository.upgradePlan(user.clubId, plan)
                
                _upgradeStatus.value = UpgradeStatus.Success
            } catch (e: Exception) {
                _upgradeStatus.value = UpgradeStatus.Error(e.message ?: "Failed to upgrade")
            }
        }
    }
}
