package com.sportclubai.domain.repository

import com.sportclubai.domain.model.ClubBranding
import com.sportclubai.domain.model.Coupon
import com.sportclubai.domain.model.Invoice
import com.sportclubai.domain.model.Subscription
import com.sportclubai.domain.model.SubscriptionPlan
import com.sportclubai.domain.model.UsageStats
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getSubscription(clubId: String): Flow<Subscription?>
    fun getUsageStats(clubId: String): Flow<UsageStats?>
    suspend fun getInvoices(clubId: String): List<Invoice>
    suspend fun applyCoupon(clubId: String, couponCode: String): Result<Coupon>
    suspend fun upgradePlan(clubId: String, newPlan: SubscriptionPlan)
    suspend fun cancelSubscription(clubId: String)
    suspend fun incrementUsage(clubId: String, metric: String, amount: Int = 1)
}
