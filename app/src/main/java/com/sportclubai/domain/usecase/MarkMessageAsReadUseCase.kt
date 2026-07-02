package com.sportclubai.domain.usecase

import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.MessageRepository
import javax.inject.Inject

class MarkMessageAsReadUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(messageId: String) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User data not found")
        val clubId = user.clubId
        messageRepository.markAsRead(clubId, messageId)
    }
}
