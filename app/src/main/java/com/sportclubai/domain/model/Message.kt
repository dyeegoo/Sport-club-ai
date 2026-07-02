package com.sportclubai.domain.model

data class Message(
    val messageId: String = "",
    val clubId: String = "",
    val senderId: String = "",
    val receiverId: String = "", // empty for group/class messages
    val classId: String = "", // empty for 1-to-1 messages
    val messageType: String = "TEXT", // TEXT, IMAGE, ANNOUNCEMENT
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val readStatus: Boolean = false,
    val attachments: List<String> = emptyList()
)
