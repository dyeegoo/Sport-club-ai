package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Message
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(message: Message) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User data not found")
        val clubId = user.clubId
        val newMessage = message.copy(senderId = uid, clubId = clubId)
        messageRepository.sendMessage(clubId, newMessage)
    }
}
