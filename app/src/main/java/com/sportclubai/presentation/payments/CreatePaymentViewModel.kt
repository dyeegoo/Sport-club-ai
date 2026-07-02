package com.sportclubai.presentation.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Payment
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.usecase.CreatePaymentUseCase
import com.sportclubai.domain.usecase.GetStudentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class CreatePaymentState {
    object Idle : CreatePaymentState()
    object Loading : CreatePaymentState()
    object Success : CreatePaymentState()
    data class Error(val message: String) : CreatePaymentState()
}

@HiltViewModel
class CreatePaymentViewModel @Inject constructor(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreatePaymentState>(CreatePaymentState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students = _students.asStateFlow()

    val selectedStudentId = MutableStateFlow("")
    val amount = MutableStateFlow("")
    val paymentType = MutableStateFlow("MONTHLY_FEE")
    val status = MutableStateFlow("PENDING")
    val dueDate = MutableStateFlow(getCurrentDate())
    val notes = MutableStateFlow("")

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            try {
                getStudentsUseCase()
                    .catch { /* ignore or handle */ }
                    .collect { studentList ->
                        _students.value = studentList
                    }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun createPayment() {
        if (selectedStudentId.value.isEmpty()) {
            _uiState.value = CreatePaymentState.Error("Please select a student")
            return
        }
        if (amount.value.isEmpty()) {
            _uiState.value = CreatePaymentState.Error("Please enter amount")
            return
        }

        viewModelScope.launch {
            _uiState.value = CreatePaymentState.Loading
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = sdf.format(Date())
                val paidDate = if (status.value == "PAID") currentDate else ""

                val payment = Payment(
                    studentId = selectedStudentId.value,
                    amount = amount.value.toDoubleOrNull() ?: 0.0,
                    paymentType = paymentType.value,
                    status = status.value,
                    dueDate = dueDate.value,
                    paidDate = paidDate,
                    notes = notes.value
                )
                createPaymentUseCase(payment)
                _uiState.value = CreatePaymentState.Success
            } catch (e: Exception) {
                _uiState.value = CreatePaymentState.Error(e.message ?: "Failed to create payment")
            }
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
