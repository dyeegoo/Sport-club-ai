package com.sportclubai.core.utils

import android.util.Log
import com.sportclubai.BuildConfig

object Logger {
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        // We log errors even in production, but route them to Crashlytics
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable)
        }
    }

    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }
}
