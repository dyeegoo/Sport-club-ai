package com.sportclubai.data.repository

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.sportclubai.domain.repository.SecurityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class SecurityRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SecurityRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun getAppCheckToken(): Result<String> {
        // Placeholder for Firebase App Check
        return Result.success("mock_app_check_token")
    }

    override fun isRooted(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }
        try {
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) return true
        } catch (e: Exception) {
            // ignore
        }
        return false
    }

    override fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT
    }

    override fun isDeveloperModeEnabled(): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) != 0
    }

    override fun saveEncryptedPreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getEncryptedPreference(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun clearSecurePreferences() {
        sharedPreferences.edit().clear().apply()
    }

    override suspend fun refreshTokenIfNeeded() {
        // Placeholder for token refresh logic
    }
}
