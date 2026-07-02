package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Payment
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetClubPaymentsUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<List<Payment>> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner" && user.role != "coach") {
            throw Exception("Unauthorized: Only owners and coaches can view club payments")
        }
        
        return paymentRepository.getClubPayments(uid)
    }
}
