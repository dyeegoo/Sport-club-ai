package com.sportclubai.domain.repository

import android.net.Uri
import com.sportclubai.domain.model.User

interface AuthRepository {
    suspend fun registerOwner(email: String, password: String): AuthResult
    suspend fun uploadProfilePhoto(uid: String, uri: Uri): String
    suspend fun saveUserToDatabase(user: User)
    
    suspend fun login(email: String, password: String): AuthResult
    suspend fun logout()
    suspend fun forgotPassword(email: String)
    suspend fun getUser(uid: String): User?
    suspend fun updateFcmToken(uid: String, token: String)
    suspend fun updateNotificationPreferences(uid: String, preferences: Map<String, Boolean>)
    fun getCurrentUserId(): String?
}

data class AuthResult(val uid: String)
