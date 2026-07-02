package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.ClubBranding
import com.sportclubai.domain.repository.WhiteLabelRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WhiteLabelRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : WhiteLabelRepository {

    override fun getClubBranding(clubId: String): Flow<ClubBranding?> = callbackFlow {
        val listener = firestore.collection("branding").document(clubId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(ClubBranding::class.java))
                } else {
                    trySend(ClubBranding(clubId = clubId)) // Default
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateClubBranding(branding: ClubBranding) {
        val id = branding.clubId.ifEmpty { throw Exception("Club ID required") }
        firestore.collection("branding").document(id).set(branding).await()
    }
}
