package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Notification
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<List<Notification>> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User data not found")
        return notificationRepository.getUserNotifications(user.clubId, uid)
    }
}
