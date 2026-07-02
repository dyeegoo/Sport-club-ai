package com.sportclubai.presentation.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Payment
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MyPaymentsState {
    object Loading : MyPaymentsState()
    data class Success(val payments: List<Payment>) : MyPaymentsState()
    data class Error(val message: String) : MyPaymentsState()
}

@HiltViewModel
class MyPaymentsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyPaymentsState>(MyPaymentsState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = MyPaymentsState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                paymentRepository.getPaymentsByStudent(user.clubId, uid)
                    .catch { e -> _uiState.value = MyPaymentsState.Error(e.message ?: "Error") }
                    .collect { list ->
                        _uiState.value = MyPaymentsState.Success(list.sortedByDescending { it.dueDate })
                    }
            } catch (e: Exception) {
                _uiState.value = MyPaymentsState.Error(e.message ?: "Failed to load data")
            }
        }
    }
}
