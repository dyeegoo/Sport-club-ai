package com.sportclubai.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.SportClass
import com.sportclubai.domain.repository.ClassRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ClassRepository {

    override fun getClassesByClub(clubId: String): Flow<List<SportClass>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId).collection("classes")
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val classes = snapshot?.documents?.mapNotNull { it.toObject(SportClass::class.java) } ?: emptyList()
            trySend(classes.sortedByDescending { it.createdAt })
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getClassById(clubId: String, classId: String): SportClass? {
        val document = firestore.collection("clubs").document(clubId)
            .collection("classes").document(classId).get().await()
        return document.toObject(SportClass::class.java)
    }

    override suspend fun createClass(clubId: String, sportClass: SportClass) {
        val collection = firestore.collection("clubs").document(clubId).collection("classes")
        val docRef = if (sportClass.classId.isEmpty()) collection.document() else collection.document(sportClass.classId)
        val newClass = if (sportClass.classId.isEmpty()) sportClass.copy(classId = docRef.id) else sportClass
        docRef.set(newClass).await()
    }

    override suspend fun updateClass(clubId: String, sportClass: SportClass) {
        firestore.collection("clubs").document(clubId)
            .collection("classes").document(sportClass.classId)
            .set(sportClass).await()
    }

    override suspend fun addStudentToClass(clubId: String, classId: String, studentId: String) {
        firestore.collection("clubs").document(clubId)
            .collection("classes").document(classId)
            .update("assignedStudents", FieldValue.arrayUnion(studentId)).await()
    }

    override suspend fun removeStudentFromClass(clubId: String, classId: String, studentId: String) {
        firestore.collection("clubs").document(clubId)
            .collection("classes").document(classId)
            .update("assignedStudents", FieldValue.arrayRemove(studentId)).await()
    }
}
