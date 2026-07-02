package com.sportclubai.domain.repository

interface FcmManager {
    suspend fun subscribeToTopic(topic: String)
    suspend fun unsubscribeFromTopic(topic: String)
    suspend fun getToken(): String?
}
