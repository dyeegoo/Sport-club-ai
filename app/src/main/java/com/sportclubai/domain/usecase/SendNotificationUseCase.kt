package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Notification
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.CloudFunctionManager
import com.sportclubai.domain.repository.NotificationRepository
import javax.inject.Inject

class SendNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository,
    private val cloudFunctionManager: CloudFunctionManager
) {
    suspend operator fun invoke(notification: Notification) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User data not found")
        
        val newNotification = notification.copy(senderId = uid, clubId = user.clubId)
        notificationRepository.sendNotification(user.clubId, newNotification)
        
        // If it's scheduled, also call the Cloud Function
        if (newNotification.scheduledAt != null && newNotification.status == "SCHEDULED") {
            cloudFunctionManager.scheduleNotification(user.clubId, newNotification.notificationId, newNotification.scheduledAt)
        }
    }
}
