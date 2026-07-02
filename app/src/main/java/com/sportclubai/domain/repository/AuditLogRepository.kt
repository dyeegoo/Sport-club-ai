package com.sportclubai.domain.repository

import com.sportclubai.domain.model.AuditLog
import kotlinx.coroutines.flow.Flow

interface AuditLogRepository {
    suspend fun logAction(clubId: String, auditLog: AuditLog)
    fun getAuditLogs(clubId: String): Flow<List<AuditLog>>
}
