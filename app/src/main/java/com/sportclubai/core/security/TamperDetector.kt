package com.sportclubai.core.security

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import java.security.MessageDigest

object TamperDetector {

    // Placeholder expected signature base64 hash
    private const val EXPECTED_SIGNATURE_HASH = "PLACEHOLDER_HASH"

    fun verifySignature(context: Context): Boolean {
        try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA-256")
                md.update(signature.toByteArray())
                val currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT).trim()
                if (EXPECTED_SIGNATURE_HASH == currentSignature) {
                    return true
                }
            }
        } catch (e: Exception) {
            // Log or handle error
        }
        return false // If it doesn't match or fails
    }

    fun isEmulator(): Boolean {
        // Basic emulator checks
        return android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || "google_sdk" == android.os.Build.PRODUCT
    }
}
