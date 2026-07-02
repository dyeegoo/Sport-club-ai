package com.sportclubai.domain.repository

interface CloudFunctionManager {
    suspend fun scheduleNotification(clubId: String, notificationId: String, scheduledTime: Long)
    suspend fun cancelScheduledNotification(clubId: String, notificationId: String)
}
