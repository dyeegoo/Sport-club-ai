package com.sportclubai.domain.repository

interface CloudFunctionsRepository {
    suspend fun checkTrialExpiration(clubId: String): Result<Boolean>
    suspend fun triggerMonthlyReset(clubId: String): Result<Boolean>
    suspend fun generateInvoice(clubId: String): Result<Boolean>
    suspend fun aggregateUsage(clubId: String): Result<Boolean>
    suspend fun validateSubscription(clubId: String): Result<Boolean>
    suspend fun updateFeatureFlags(clubId: String): Result<Boolean>
}
