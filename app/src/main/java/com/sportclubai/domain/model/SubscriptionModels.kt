package com.sportclubai.domain.model

enum class SubscriptionPlan {
    FREE, STARTER, PROFESSIONAL, BUSINESS, ENTERPRISE
}

enum class FeatureFlag {
    AI_TRAINING,
    ANALYTICS,
    NOTIFICATIONS,
    MESSAGING,
    PARENTS_ACCESS,
    MEDIA_STORAGE,
    REPORTS,
    PDF_EXPORT,
    API_ACCESS,
    WHITE_LABEL,
    CUSTOM_DOMAIN
}

data class PlanLimits(
    val maxStudents: Int = 50,
    val maxCoaches: Int = 2,
    val maxClasses: Int = 10,
    val maxMonthlyAiRequests: Int = 0,
    val maxStorageMb: Int = 500,
    val maxAdmins: Int = 1,
    val maxParents: Int = 50,
    val maxPushNotifications: Int = 1000
)

data class Subscription(
    val id: String = "",
    val clubId: String = "",
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
    val limits: PlanLimits = PlanLimits(),
    val enabledFeatures: List<FeatureFlag> = emptyList(),
    val isTrial: Boolean = false,
    val trialStartDate: Long? = null,
    val trialEndDate: Long? = null,
    val currentPeriodStart: Long = 0,
    val currentPeriodEnd: Long = 0,
    val isActive: Boolean = true,
    val autoRenew: Boolean = true,
    val cancelAtPeriodEnd: Boolean = false
)

data class UsageStats(
    val clubId: String = "",
    val studentCount: Int = 0,
    val coachCount: Int = 0,
    val classCount: Int = 0,
    val currentMonthlyAiRequests: Int = 0,
    val currentStorageMb: Int = 0,
    val currentPushNotifications: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class ClubBranding(
    val clubId: String = "",
    val appName: String? = null,
    val primaryColorHex: String? = null,
    val secondaryColorHex: String? = null,
    val logoUrl: String? = null,
    val customDomain: String? = null
)

data class Invoice(
    val id: String = "",
    val clubId: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val status: String = "PAID",
    val date: Long = System.currentTimeMillis(),
    val invoiceUrl: String? = null
)

data class Coupon(
    val code: String = "",
    val discountPercentage: Int = 0,
    val discountAmount: Double = 0.0,
    val validUntil: Long = 0
)
