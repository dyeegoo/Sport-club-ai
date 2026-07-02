package com.sportclubai.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAnalytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun logLogin(userId: String, method: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        firebaseAnalytics.setUserId(userId)
    }

    fun logSubscriptionUpgrade(planId: String) {
        val bundle = Bundle().apply {
            putString("plan_id", planId)
        }
        firebaseAnalytics.logEvent("subscription_upgrade", bundle)
    }

    fun logAttendanceUsage(classId: String) {
        val bundle = Bundle().apply {
            putString("class_id", classId)
        }
        firebaseAnalytics.logEvent("attendance_usage", bundle)
    }

    fun logAiTrainingGeneration(topic: String) {
        val bundle = Bundle().apply {
            putString("topic", topic)
        }
        firebaseAnalytics.logEvent("ai_training_generation", bundle)
    }

    fun logPaymentCreation(amount: Double, currency: String) {
        val bundle = Bundle().apply {
            putDouble(FirebaseAnalytics.Param.VALUE, amount)
            putString(FirebaseAnalytics.Param.CURRENCY, currency)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, bundle)
    }
}
