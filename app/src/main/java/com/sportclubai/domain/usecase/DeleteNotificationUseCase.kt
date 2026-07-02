package com.sportclubai.domain.usecase

import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.NotificationRepository
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(notificationId: String) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User data not found")
        notificationRepository.deleteNotification(user.clubId, notificationId)
    }
}
