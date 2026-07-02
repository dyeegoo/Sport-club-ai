package com.sportclubai.data.remote

import com.sportclubai.domain.repository.CloudFunctionManager
import javax.inject.Inject

class CloudFunctionManagerImpl @Inject constructor() : CloudFunctionManager {

    override suspend fun scheduleNotification(clubId: String, notificationId: String, scheduledTime: Long) {
        // Implementation for calling Firebase Cloud Function to schedule a notification
        // e.g. functions.getHttpsCallable("scheduleNotification").call(data)
    }

    override suspend fun cancelScheduledNotification(clubId: String, notificationId: String) {
        // Implementation for calling Firebase Cloud Function to cancel
    }
}
