package com.sportclubai.domain.model

data class User(
    val uid: String = "",
    val role: String = "owner",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val profilePhotoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val fcmToken: String = "",
    val clubId: String = "",
    val notificationPreferences: Map<String, Boolean> = mapOf(
        "NEW_MESSAGE" to true,
        "PAYMENT_REMINDER" to true,
        "ATTENDANCE_WARNING" to true,
        "CLUB_ANNOUNCEMENT" to true
    )
)
