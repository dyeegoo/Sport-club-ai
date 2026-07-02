package com.sportclubai.domain.repository

import com.sportclubai.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getUserNotifications(clubId: String, userId: String, limit: Long = 50L): Flow<List<Notification>>
    suspend fun sendNotification(clubId: String, notification: Notification)
    suspend fun markAsRead(clubId: String, notificationId: String, userId: String)
    suspend fun markAllAsRead(clubId: String, userId: String)
    suspend fun deleteNotification(clubId: String, notificationId: String)
    suspend fun bulkDelete(clubId: String, notificationIds: List<String>)
    suspend fun getScheduledNotifications(clubId: String): Flow<List<Notification>>
}
