package com.sportclubai.domain.repository

import com.sportclubai.domain.model.SubscriptionPlan

interface BillingRepository {
    suspend fun initiateStripePayment(clubId: String, plan: SubscriptionPlan): Result<String>
    suspend fun initiateGooglePlayBilling(clubId: String, plan: SubscriptionPlan): Result<String>
    suspend fun initiateAppleBilling(clubId: String, plan: SubscriptionPlan): Result<String>
    suspend fun initiatePayPalPayment(clubId: String, plan: SubscriptionPlan): Result<String>
    suspend fun requestManualInvoice(clubId: String, plan: SubscriptionPlan): Result<Boolean>
}
