package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.Message
import com.sportclubai.domain.repository.MessageRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessageRepository {

    override fun getMessagesBetweenUsers(clubId: String, userId1: String, userId2: String): Flow<List<Message>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId).collection("messages")
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
            val filtered = messages.filter {
                (it.senderId == userId1 && it.receiverId == userId2) ||
                (it.senderId == userId2 && it.receiverId == userId1)
            }.sortedBy { it.timestamp }
            trySend(filtered)
        }
        awaitClose { listener.remove() }
    }

    override fun getClassMessages(clubId: String, classId: String): Flow<List<Message>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId).collection("messages")
            .whereEqualTo("classId", classId)
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
            trySend(messages.sortedBy { it.timestamp })
        }
        awaitClose { listener.remove() }
    }

    override fun getUserConversations(clubId: String, userId: String): Flow<List<Message>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId).collection("messages")
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
            // Filter all messages that the user should see
            val filtered = messages.filter {
                it.senderId == userId || it.receiverId == userId || it.messageType == "ANNOUNCEMENT" || it.classId.isNotEmpty()
            }.sortedByDescending { it.timestamp }
            trySend(filtered)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(clubId: String, message: Message) {
        val collection = firestore.collection("clubs").document(clubId).collection("messages")
        val docRef = if (message.messageId.isEmpty()) collection.document() else collection.document(message.messageId)
        val newMessage = if (message.messageId.isEmpty()) message.copy(messageId = docRef.id) else message
        docRef.set(newMessage).await()
    }

    override suspend fun markAsRead(clubId: String, messageId: String) {
        firestore.collection("clubs").document(clubId)
            .collection("messages").document(messageId)
            .update("readStatus", true).await()
    }
}
