package com.sportclubai.core.security

import android.app.Activity
import android.view.WindowManager
import com.sportclubai.domain.repository.SecurityRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    fun applyScreenshotProtection(activity: Activity, isSensitive: Boolean) {
        if (isSensitive) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun isEnvironmentSafe(): Boolean {
        return !securityRepository.isRooted() && !securityRepository.isEmulator()
    }

    fun checkSecurityViolations(): List<String> {
        val violations = mutableListOf<String>()
        if (securityRepository.isRooted()) violations.add("Root access detected")
        if (securityRepository.isEmulator()) violations.add("Emulator environment detected")
        return violations
    }
}
