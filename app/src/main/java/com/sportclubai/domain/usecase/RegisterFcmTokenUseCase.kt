package com.sportclubai.domain.usecase

import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.FcmManager
import javax.inject.Inject

class RegisterFcmTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val fcmManager: FcmManager
) {
    suspend operator fun invoke() {
        val uid = authRepository.getCurrentUserId() ?: return
        val token = fcmManager.getToken() ?: return
        // Since AuthRepository doesn't have an updateFcmToken, we'll update user directly if possible.
        // We assume we can add updateFcmToken to AuthRepository.
        authRepository.updateFcmToken(uid, token)
    }
}
