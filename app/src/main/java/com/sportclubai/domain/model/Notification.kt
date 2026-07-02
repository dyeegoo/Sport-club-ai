package com.sportclubai.domain.model

data class Notification(
    val notificationId: String = "",
    val clubId: String = "",
    val senderId: String = "",
    val receiverIds: List<String> = emptyList(), // empty means broadcast to club, otherwise list of user IDs
    val title: String = "",
    val body: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val priority: String = "NORMAL", // HIGH, NORMAL, LOW
    val relatedEntityId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null,
    val readBy: List<String> = emptyList(),
    val status: String = "SENT" // SCHEDULED, SENT
)

enum class NotificationType {
    NEW_MESSAGE,
    PAYMENT_REMINDER,
    ATTENDANCE_WARNING,
    UPCOMING_CLASS,
    CLASS_CANCELLED,
    BELT_EXAM_REMINDER,
    BELT_EXAM_RESULT,
    COACH_ANNOUNCEMENT,
    CLUB_ANNOUNCEMENT,
    NEW_STUDENT_REGISTRATION,
    AI_RECOMMENDATION,
    SYSTEM
}
