package com.sportclubai.domain.repository

import com.sportclubai.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessagesBetweenUsers(clubId: String, userId1: String, userId2: String): Flow<List<Message>>
    fun getClassMessages(clubId: String, classId: String): Flow<List<Message>>
    fun getUserConversations(clubId: String, userId: String): Flow<List<Message>>
    suspend fun sendMessage(clubId: String, message: Message)
    suspend fun markAsRead(clubId: String, messageId: String)
}
