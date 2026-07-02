package com.sportclubai

import android.app.Application
import com.sportclubai.core.crash.CrashReporter
import com.sportclubai.core.crash.GlobalExceptionHandler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SportClubApp : Application() {
    @Inject
    lateinit var crashReporter: CrashReporter

    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHandler.initialize(this, crashReporter)
    }
}
