package com.sportclubai.core.crash

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import androidx.navigation.navArgument
import androidx.navigation.NavType

@Singleton
class CrashReporter @Inject constructor() {
    
    fun logException(throwable: Throwable) {
        // Placeholder for FirebaseCrashlytics.getInstance().recordException(throwable)
        if (!android.support.v4.os.BuildCompat.isAtLeastN()) {
            Log.e("CrashReporter", "Exception logged", throwable)
        }
    }

    fun log(message: String) {
        // Placeholder for FirebaseCrashlytics.getInstance().log(message)
        Log.d("CrashReporter", message)
    }

    fun setUserId(userId: String) {
        // Placeholder for FirebaseCrashlytics.getInstance().setUserId(userId)
    }
}
