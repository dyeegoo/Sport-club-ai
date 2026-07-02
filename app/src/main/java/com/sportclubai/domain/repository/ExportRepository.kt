package com.sportclubai.domain.repository

import kotlinx.coroutines.flow.Flow

interface ExportRepository {
    suspend fun requestExport(clubId: String, type: String, format: String): Result<String>
    fun getExportStatus(clubId: String, exportId: String): Flow<String>
}
