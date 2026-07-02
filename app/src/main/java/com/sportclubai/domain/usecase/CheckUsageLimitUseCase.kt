package com.sportclubai.domain.usecase

import com.sportclubai.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CheckUsageLimitUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) {
    suspend fun canAddStudent(clubId: String): Boolean {
        val sub = subscriptionRepository.getSubscription(clubId).firstOrNull() ?: return false
        val usage = subscriptionRepository.getUsageStats(clubId).firstOrNull() ?: return true
        return usage.studentCount < sub.limits.maxStudents
    }

    suspend fun canAddCoach(clubId: String): Boolean {
        val sub = subscriptionRepository.getSubscription(clubId).firstOrNull() ?: return false
        val usage = subscriptionRepository.getUsageStats(clubId).firstOrNull() ?: return true
        return usage.coachCount < sub.limits.maxCoaches
    }

    suspend fun canUseAI(clubId: String): Boolean {
        val sub = subscriptionRepository.getSubscription(clubId).firstOrNull() ?: return false
        val usage = subscriptionRepository.getUsageStats(clubId).firstOrNull() ?: return true
        return usage.currentMonthlyAiRequests < sub.limits.maxMonthlyAiRequests
    }
}
