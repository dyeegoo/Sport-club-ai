package com.sportclubai.domain.repository

interface SecurityRepository {
    suspend fun getAppCheckToken(): Result<String>
    fun isRooted(): Boolean
    fun isEmulator(): Boolean
    fun isDeveloperModeEnabled(): Boolean
    fun saveEncryptedPreference(key: String, value: String)
    fun getEncryptedPreference(key: String): String?
    fun clearSecurePreferences()
    suspend fun refreshTokenIfNeeded()
}
