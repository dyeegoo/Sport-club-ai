package com.sportclubai.presentation.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Payment
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.usecase.GetClubPaymentsUseCase
import com.sportclubai.domain.usecase.GetStudentsUseCase
import com.sportclubai.domain.usecase.UpdatePaymentStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class PaymentItem(
    val payment: Payment,
    val student: Student?
)

sealed class PaymentsState {
    object Loading : PaymentsState()
    data class Success(
        val payments: List<PaymentItem>,
        val totalRevenue: Double,
        val outstandingAmount: Double,
        val overduePayments: List<PaymentItem>
    ) : PaymentsState()
    data class Error(val message: String) : PaymentsState()
}

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val getClubPaymentsUseCase: GetClubPaymentsUseCase,
    private val getStudentsUseCase: GetStudentsUseCase,
    private val updatePaymentStatusUseCase: UpdatePaymentStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentsState>(PaymentsState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = PaymentsState.Loading
            try {
                val paymentsFlow = getClubPaymentsUseCase()
                val studentsFlow = getStudentsUseCase()

                paymentsFlow.combine(studentsFlow) { payments, students ->
                    val items = payments.map { payment ->
                        PaymentItem(
                            payment = payment,
                            student = students.find { it.studentId == payment.studentId }
                        )
                    }
                    
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val currentDate = sdf.format(Date())
                    
                    items.forEach { item ->
                        if (item.payment.status == "PENDING" && item.payment.dueDate < currentDate) {
                            updatePaymentStatusUseCase(item.payment.paymentId, "OVERDUE")
                        }
                    }
                    
                    val currentMonth = currentDate.substring(0, 7) // YYYY-MM
                    val totalRevenue = items
                        .filter { it.payment.status == "PAID" && it.payment.paidDate.startsWith(currentMonth) }
                        .sumOf { it.payment.amount }
                        
                    val outstandingAmount = items
                        .filter { it.payment.status == "PENDING" || it.payment.status == "OVERDUE" }
                        .sumOf { it.payment.amount }
                        
                    val overduePayments = items.filter { it.payment.status == "OVERDUE" }

                    PaymentsState.Success(items, totalRevenue, outstandingAmount, overduePayments)
                }.catch { e ->
                    _uiState.value = PaymentsState.Error(e.message ?: "Failed to load payments")
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = PaymentsState.Error(e.message ?: "Failed to load payments")
            }
        }
    }

    fun markAsPaid(paymentId: String) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        viewModelScope.launch {
            try {
                updatePaymentStatusUseCase(paymentId, "PAID", currentDate)
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}
