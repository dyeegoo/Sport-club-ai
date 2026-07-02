package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.repository.CoachRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CoachRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CoachRepository {

    override fun getCoachesByClub(clubId: String): Flow<List<Coach>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId).collection("coaches")
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val coaches = snapshot?.documents?.mapNotNull { it.toObject(Coach::class.java) } ?: emptyList()
            trySend(coaches.sortedByDescending { it.joinDate })
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getCoachById(clubId: String, coachId: String): Coach? {
        val document = firestore.collection("clubs").document(clubId)
            .collection("coaches").document(coachId).get().await()
        return document.toObject(Coach::class.java)
    }

    override suspend fun addCoach(clubId: String, coach: Coach) {
        val collection = firestore.collection("clubs").document(clubId).collection("coaches")
        val docRef = if (coach.coachId.isEmpty()) collection.document() else collection.document(coach.coachId)
        val newCoach = if (coach.coachId.isEmpty()) coach.copy(coachId = docRef.id) else coach
        docRef.set(newCoach).await()
    }

    override suspend fun updateCoach(clubId: String, coach: Coach) {
        firestore.collection("clubs").document(clubId)
            .collection("coaches").document(coach.coachId)
            .set(coach).await()
    }

    override suspend fun deleteCoach(clubId: String, coachId: String) {
        firestore.collection("clubs").document(clubId)
            .collection("coaches").document(coachId)
            .delete().await()
    }
}
