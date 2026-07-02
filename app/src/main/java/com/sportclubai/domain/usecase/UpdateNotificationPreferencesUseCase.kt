package com.sportclubai.domain.usecase

import com.sportclubai.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateNotificationPreferencesUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(preferences: Map<String, Boolean>) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        authRepository.updateNotificationPreferences(uid, preferences)
    }
}
