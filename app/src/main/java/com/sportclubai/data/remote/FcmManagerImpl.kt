package com.sportclubai.data.remote

import com.sportclubai.domain.repository.FcmManager
import javax.inject.Inject

class FcmManagerImpl @Inject constructor() : FcmManager {
    override suspend fun subscribeToTopic(topic: String) {
        // Implementation for FCM topic subscription
    }

    override suspend fun unsubscribeFromTopic(topic: String) {
        // Implementation for FCM topic unsubscription
    }

    override suspend fun getToken(): String? {
        // Implementation to fetch device token
        return null
    }
}
