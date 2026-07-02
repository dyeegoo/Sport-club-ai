package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sportclubai.domain.model.BackupMetadata
import com.sportclubai.domain.repository.BackupRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BackupRepository {

    override suspend fun triggerManualBackup(clubId: String): Result<Boolean> {
        // In a real app, this would call a Cloud Function
        return Result.success(true)
    }

    override fun getBackupHistory(clubId: String): Flow<List<BackupMetadata>> = callbackFlow {
        val listener = firestore.collection("clubs").document(clubId).collection("backups")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val backups = snapshot.documents.mapNotNull { it.toObject(BackupMetadata::class.java) }
                    trySend(backups)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun restoreBackup(clubId: String, backupId: String): Result<Boolean> {
        // In a real app, this would call a Cloud Function
        return Result.success(true)
    }
}
