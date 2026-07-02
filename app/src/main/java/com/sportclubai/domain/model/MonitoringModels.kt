package com.sportclubai.domain.model

data class MonitoringStats(
    val clubId: String = "",
    val activeUsers: Int = 0,
    val firestoreReads: Long = 0,
    val firestoreWrites: Long = 0,
    val storageBytesUsed: Long = 0,
    val aiRequestsCount: Long = 0,
    val errorCount: Long = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
