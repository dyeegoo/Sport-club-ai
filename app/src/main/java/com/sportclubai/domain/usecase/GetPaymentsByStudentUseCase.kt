package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Payment
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaymentsByStudentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(studentId: String): Flow<List<Payment>> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        
        // We assume uid is clubId or owner/coach. 
        // In a real multi-role system we'd check if the user is a student requesting their own.
        return paymentRepository.getPaymentsByStudent(uid, studentId)
    }
}
