package com.sportclubai.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sportclubai.domain.model.User
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.AuthResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRepository {
    override suspend fun registerOwner(email: String, password: String): AuthResult {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Registration failed")
        return AuthResult(uid)
    }

    override suspend fun uploadProfilePhoto(uid: String, uri: Uri): String {
        val ref = storage.reference.child("profiles/$uid.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    override suspend fun saveUserToDatabase(user: User) {
        firestore.collection("users").document(user.uid).set(user).await()
    }

    override suspend fun login(email: String, password: String): AuthResult {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Login failed")
        return AuthResult(uid)
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun getUser(uid: String): User? {
        val document = firestore.collection("users").document(uid).get().await()
        return document.toObject(User::class.java)
    }

    override suspend fun updateFcmToken(uid: String, token: String) {
        firestore.collection("users").document(uid).update("fcmToken", token).await()
    }

    override suspend fun updateNotificationPreferences(uid: String, preferences: Map<String, Boolean>) {
        firestore.collection("users").document(uid).update("notificationPreferences", preferences).await()
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
