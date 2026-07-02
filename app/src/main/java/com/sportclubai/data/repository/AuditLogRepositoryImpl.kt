package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sportclubai.domain.model.AuditLog
import com.sportclubai.domain.repository.AuditLogRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuditLogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AuditLogRepository {

    override suspend fun logAction(clubId: String, auditLog: AuditLog) {
        val ref = firestore.collection("clubs").document(clubId).collection("audit_logs").document()
        val logWithId = auditLog.copy(id = ref.id)
        ref.set(logWithId).await()
    }

    override fun getAuditLogs(clubId: String): Flow<List<AuditLog>> = callbackFlow {
        val listener = firestore.collection("clubs").document(clubId).collection("audit_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val logs = snapshot.documents.mapNotNull { it.toObject(AuditLog::class.java) }
                    trySend(logs)
                }
            }
        awaitClose { listener.remove() }
    }
}
