package com.sportclubai.presentation.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Invoice
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BillingHistoryState {
    object Loading : BillingHistoryState()
    data class Success(val invoices: List<Invoice>) : BillingHistoryState()
    data class Error(val message: String) : BillingHistoryState()
}

@HiltViewModel
class BillingHistoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BillingHistoryState>(BillingHistoryState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadInvoices()
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _uiState.value = BillingHistoryState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                val invoices = subscriptionRepository.getInvoices(user.clubId)
                _uiState.value = BillingHistoryState.Success(invoices)
            } catch (e: Exception) {
                _uiState.value = BillingHistoryState.Error(e.message ?: "Failed to load invoices")
            }
        }
    }
}
