package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Message
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserConversationsUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<List<Message>> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User data not found")
        val clubId = user.clubId
        return messageRepository.getUserConversations(clubId, uid)
    }
}
