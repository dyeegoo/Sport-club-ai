package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.Exam
import com.sportclubai.domain.model.ExamStatus
import com.sportclubai.domain.model.ExamStudent
import com.sportclubai.domain.repository.ExamRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExamRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ExamRepository {

    override fun getExamsForClub(clubId: String): Flow<List<Exam>> = callbackFlow {
        val listener = firestore.collection("exams")
            .whereEqualTo("clubId", clubId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val exams = snapshot.documents.mapNotNull { it.toObject(Exam::class.java) }
                    trySend(exams.sortedByDescending { it.date })
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getUpcomingExams(clubId: String): Flow<List<Exam>> = callbackFlow {
        val listener = firestore.collection("exams")
            .whereEqualTo("clubId", clubId)
            .whereIn("status", listOf(ExamStatus.SCHEDULED.name, ExamStatus.IN_PROGRESS.name))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val exams = snapshot.documents.mapNotNull { it.toObject(Exam::class.java) }
                    trySend(exams.sortedBy { it.date })
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getExamById(examId: String): Exam? {
        val doc = firestore.collection("exams").document(examId).get().await()
        return doc.toObject(Exam::class.java)
    }

    override suspend fun createOrUpdateExam(exam: Exam) {
        val id = exam.id.ifEmpty { firestore.collection("exams").document().id }
        firestore.collection("exams").document(id).set(exam.copy(id = id)).await()
    }

    override suspend fun deleteExam(examId: String) {
        firestore.collection("exams").document(examId).delete().await()
    }

    override suspend fun updateStudentEvaluation(examId: String, student: ExamStudent) {
        firestore.runTransaction { transaction ->
            val ref = firestore.collection("exams").document(examId)
            val snapshot = transaction.get(ref)
            val exam = snapshot.toObject(Exam::class.java) ?: return@runTransaction
            
            val updatedStudents = exam.students.map {
                if (it.studentId == student.studentId) student else it
            }
            transaction.set(ref, exam.copy(students = updatedStudents))
        }.await()
    }
}
