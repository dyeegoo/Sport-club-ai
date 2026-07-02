package com.sportclubai.domain.usecase

import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.PaymentRepository
import javax.inject.Inject

class UpdatePaymentStatusUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(paymentId: String, status: String, paidDate: String = "") {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner") {
            throw Exception("Unauthorized: Only owners can update payment status")
        }
        
        paymentRepository.updatePaymentStatus(uid, paymentId, status, paidDate)
    }
}
