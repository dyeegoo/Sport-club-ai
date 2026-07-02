package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Payment
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.PaymentRepository
import javax.inject.Inject

class CreatePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(payment: Payment) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner") {
            throw Exception("Unauthorized: Only owners can create payments")
        }
        
        val newPayment = payment.copy(createdBy = uid)
        paymentRepository.createPayment(uid, newPayment)
    }
}
