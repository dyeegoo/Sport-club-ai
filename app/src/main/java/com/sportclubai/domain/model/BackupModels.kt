package com.sportclubai.domain.model

data class BackupMetadata(
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val sizeBytes: Long = 0,
    val status: String = "pending", // pending, success, failed
    val type: String = "manual", // manual, automatic
    val url: String? = null
)
