package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.FeatureFlag
import com.sportclubai.domain.model.SubscriptionPlan
import com.sportclubai.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CheckFeatureAccessUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) {
    suspend operator fun invoke(clubId: String, feature: FeatureFlag): Boolean {
        val subscription = subscriptionRepository.getSubscription(clubId).firstOrNull() ?: return false
        
        // If they are on enterprise, everything is true usually, or if the feature is in enabledFeatures
        if (subscription.plan == SubscriptionPlan.ENTERPRISE) return true
        
        if (!subscription.isActive) return false

        return subscription.enabledFeatures.contains(feature)
    }
}
