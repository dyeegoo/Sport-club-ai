package com.sportclubai.domain.usecase

import com.sportclubai.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String) {
        authRepository.forgotPassword(email)
    }
}
