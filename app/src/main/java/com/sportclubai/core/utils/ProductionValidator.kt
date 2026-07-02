package com.sportclubai.core.utils

import com.sportclubai.BuildConfig

object ProductionValidator {

    data class ValidationResult(val isReady: Boolean, val messages: List<String>)

    fun validate(): ValidationResult {
        val messages = mutableListOf<String>()
        var isReady = true

        if (BuildConfig.DEBUG) {
            isReady = false
            messages.add("ERROR: Build is in DEBUG mode. Must be RELEASE for production.")
        }

        // Add any other runtime checks here (e.g. Firebase initialized, environment keys present)
        
        if (isReady) {
            messages.add("SUCCESS: App is configured for production.")
        }

        return ValidationResult(isReady, messages)
    }
}
