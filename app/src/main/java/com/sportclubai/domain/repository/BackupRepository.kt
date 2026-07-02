package com.sportclubai.domain.repository

import com.sportclubai.domain.model.BackupMetadata
import kotlinx.coroutines.flow.Flow

interface BackupRepository {
    suspend fun triggerManualBackup(clubId: String): Result<Boolean>
    fun getBackupHistory(clubId: String): Flow<List<BackupMetadata>>
    suspend fun restoreBackup(clubId: String, backupId: String): Result<Boolean>
}
