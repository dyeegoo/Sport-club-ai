package com.sportclubai.core.crash

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlin.system.exitProcess

class GlobalExceptionHandler(
    private val context: Context,
    private val crashReporter: CrashReporter,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            // Log to Crashlytics or our custom reporter
            crashReporter.logException(e)
            Log.e("GlobalExceptionHandler", "App crashed", e)

            // Optionally, launch a fallback/crash screen instead of immediate exit
            // For now, we delegate to the default handler to allow standard crash reporting mechanisms
        } catch (ex: Exception) {
            Log.e("GlobalExceptionHandler", "Error in custom exception handler", ex)
        } finally {
            defaultHandler?.uncaughtException(t, e) ?: exitProcess(1)
        }
    }

    companion object {
        fun initialize(context: Context, crashReporter: CrashReporter) {
            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (defaultHandler !is GlobalExceptionHandler) {
                Thread.setDefaultUncaughtExceptionHandler(
                    GlobalExceptionHandler(context.applicationContext, crashReporter, defaultHandler)
                )
            }
        }
    }
}
