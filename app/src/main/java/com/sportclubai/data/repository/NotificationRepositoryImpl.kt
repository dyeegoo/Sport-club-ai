package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.sportclubai.domain.model.Notification
import com.sportclubai.domain.repository.NotificationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    override fun getUserNotifications(clubId: String, userId: String, limit: Long): Flow<List<Notification>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId).collection("notifications")
        
        // Wait, filtering and sorting locally works best if we don't have complex indexes set up,
        // so applying the limit on the snapshot list is safer without index creation.
        // We limit the output. Alternatively we could order by createdAt but that requires an index if mixed with array-contains.
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            
            val notifications = snapshot?.documents?.mapNotNull { it.toObject(Notification::class.java) } ?: emptyList()
            
            val currentTime = System.currentTimeMillis()
            
            val filtered = notifications.filter {
                (it.scheduledAt == null || it.scheduledAt <= currentTime || it.status == "SENT") &&
                (it.receiverIds.isEmpty() || it.receiverIds.contains(userId) || it.senderId == userId)
            }.sortedByDescending { it.createdAt }.take(limit.toInt())
            
            trySend(filtered)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun sendNotification(clubId: String, notification: Notification) {
        val collection = firestore.collection("clubs").document(clubId).collection("notifications")
        val docRef = if (notification.notificationId.isEmpty()) collection.document() else collection.document(notification.notificationId)
        val newNotification = if (notification.notificationId.isEmpty()) notification.copy(notificationId = docRef.id) else notification
        docRef.set(newNotification).await()
    }

    override suspend fun markAsRead(clubId: String, notificationId: String, userId: String) {
        firestore.collection("clubs").document(clubId)
            .collection("notifications").document(notificationId)
            .update("readBy", FieldValue.arrayUnion(userId)).await()
    }

    override suspend fun markAllAsRead(clubId: String, userId: String) {
        val collection = firestore.collection("clubs").document(clubId).collection("notifications")
        val snapshot = collection.get().await()
        val batch = firestore.batch()
        snapshot.documents.forEach { doc ->
            val notification = doc.toObject(Notification::class.java)
            if (notification != null && 
               (notification.receiverIds.isEmpty() || notification.receiverIds.contains(userId)) && 
               !notification.readBy.contains(userId)
            ) {
                batch.update(doc.reference, "readBy", FieldValue.arrayUnion(userId))
            }
        }
        batch.commit().await()
    }

    override suspend fun deleteNotification(clubId: String, notificationId: String) {
        firestore.collection("clubs").document(clubId)
            .collection("notifications").document(notificationId)
            .delete().await()
    }

    override suspend fun bulkDelete(clubId: String, notificationIds: List<String>) {
        val collection = firestore.collection("clubs").document(clubId).collection("notifications")
        val batch = firestore.batch()
        notificationIds.forEach { id ->
            batch.delete(collection.document(id))
        }
        batch.commit().await()
    }

    override fun getScheduledNotifications(clubId: String): Flow<List<Notification>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId).collection("notifications")
            .whereEqualTo("status", "SCHEDULED")
        
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val notifications = snapshot?.documents?.mapNotNull { it.toObject(Notification::class.java) } ?: emptyList()
            trySend(notifications)
        }
        awaitClose { listener.remove() }
    }
}
