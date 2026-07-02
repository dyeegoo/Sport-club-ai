package com.sportclubai.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.repository.StudentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : StudentRepository {

    override fun getStudentsByClub(clubId: String): Flow<List<Student>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId).collection("students")
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val students = snapshot?.documents?.mapNotNull { it.toObject(Student::class.java) } ?: emptyList()
            trySend(students)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getStudentById(clubId: String, studentId: String): Student? {
        val document = firestore.collection("clubs").document(clubId).collection("students").document(studentId).get().await()
        return document.toObject(Student::class.java)
    }

    override suspend fun addStudent(clubId: String, student: Student, imageUri: Uri?): String {
        val studentId = firestore.collection("clubs").document(clubId).collection("students").document().id
        var profileImageUrl = student.profileImage

        if (imageUri != null) {
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("clubs/$clubId/students/$studentId/$fileName")
            val uploadTask = storageRef.putFile(imageUri).await()
            profileImageUrl = uploadTask.storage.downloadUrl.await().toString()
        }

        val newStudent = student.copy(studentId = studentId, profileImage = profileImageUrl, clubId = clubId)
        firestore.collection("clubs").document(clubId).collection("students").document(studentId).set(newStudent).await()
        return studentId
    }

    override suspend fun updateStudent(clubId: String, student: Student, imageUri: Uri?) {
        var profileImageUrl = student.profileImage

        if (imageUri != null) {
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("clubs/$clubId/students/${student.studentId}/$fileName")
            val uploadTask = storageRef.putFile(imageUri).await()
            profileImageUrl = uploadTask.storage.downloadUrl.await().toString()
        }

        val updatedStudent = student.copy(profileImage = profileImageUrl)
        firestore.collection("clubs").document(clubId).collection("students").document(student.studentId).set(updatedStudent).await()
    }

    override suspend fun deleteStudent(clubId: String, studentId: String) {
        firestore.collection("clubs").document(clubId).collection("students").document(studentId).delete().await()
    }
}
