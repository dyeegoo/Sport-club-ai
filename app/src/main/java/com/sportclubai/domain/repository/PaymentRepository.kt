package com.sportclubai.domain.repository

import com.sportclubai.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getPaymentsByStudent(clubId: String, studentId: String): Flow<List<Payment>>
    fun getClubPayments(clubId: String): Flow<List<Payment>>
    suspend fun createPayment(clubId: String, payment: Payment)
    suspend fun updatePaymentStatus(clubId: String, paymentId: String, status: String, paidDate: String = "")
}
