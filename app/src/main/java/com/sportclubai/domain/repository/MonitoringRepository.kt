package com.sportclubai.domain.repository

import com.sportclubai.domain.model.MonitoringStats
import kotlinx.coroutines.flow.Flow

interface MonitoringRepository {
    fun getMonitoringStats(clubId: String): Flow<MonitoringStats>
    suspend fun logError(clubId: String, errorMessage: String, stackTrace: String)
    suspend fun logPerformanceMetric(clubId: String, metricName: String, durationMs: Long)
}
