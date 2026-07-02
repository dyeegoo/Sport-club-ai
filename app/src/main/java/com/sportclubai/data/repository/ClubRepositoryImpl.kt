package com.sportclubai.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sportclubai.domain.model.Club
import com.sportclubai.domain.repository.ClubRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ClubRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ClubRepository {
    override suspend fun generateClubId(): String {
        return firestore.collection("clubs").document().id
    }

    override suspend fun uploadClubLogo(clubId: String, uri: Uri): String {
        val ref = storage.reference.child("clubs/$clubId/logo.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    override suspend fun saveClub(club: Club) {
        firestore.collection("clubs").document(club.clubId).set(club).await()
    }
}
