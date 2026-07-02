package com.sportclubai.domain.model

data class AuditLog(
    val id: String = "",
    val userId: String = "",
    val role: String = "",
    val action: String = "",
    val entity: String = "",
    val entityId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val deviceInfo: String = "",
    val ipPlaceholder: String = "127.0.0.1"
)
