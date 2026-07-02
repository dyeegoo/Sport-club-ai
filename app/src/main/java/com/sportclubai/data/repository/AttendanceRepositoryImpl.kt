package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.Attendance
import com.sportclubai.domain.repository.AttendanceRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AttendanceRepository {

    override fun getAttendanceByDate(clubId: String, date: String): Flow<List<Attendance>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId)
            .collection("attendance")
            .whereEqualTo("date", date)
            
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val attendances = snapshot?.documents?.mapNotNull { it.toObject(Attendance::class.java) } ?: emptyList()
            trySend(attendances)
        }
        awaitClose { listener.remove() }
    }

    override fun getStudentAttendanceHistory(clubId: String, studentId: String): Flow<List<Attendance>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId)
            .collection("attendance")
            .whereEqualTo("studentId", studentId)
            
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val attendances = snapshot?.documents?.mapNotNull { it.toObject(Attendance::class.java) } ?: emptyList()
            trySend(attendances.sortedByDescending { it.date })
        }
        awaitClose { listener.remove() }
    }

    override fun getAttendanceSummary(clubId: String, startDate: String, endDate: String): Flow<List<Attendance>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId)
            .collection("attendance")
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val attendances = snapshot?.documents?.mapNotNull { it.toObject(Attendance::class.java) } ?: emptyList()
            trySend(attendances)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun markAttendance(clubId: String, attendance: Attendance) {
        val collection = firestore.collection("clubs").document(clubId).collection("attendance")
        val docRef = if (attendance.attendanceId.isEmpty()) collection.document() else collection.document(attendance.attendanceId)
        val newAttendance = if (attendance.attendanceId.isEmpty()) attendance.copy(attendanceId = docRef.id) else attendance
        docRef.set(newAttendance).await()
    }

    override suspend fun markBulkAttendance(clubId: String, attendances: List<Attendance>) {
        val batch = firestore.batch()
        val collection = firestore.collection("clubs").document(clubId).collection("attendance")
        
        for (attendance in attendances) {
            val docRef = if (attendance.attendanceId.isEmpty()) collection.document() else collection.document(attendance.attendanceId)
            val newAttendance = if (attendance.attendanceId.isEmpty()) attendance.copy(attendanceId = docRef.id) else attendance
            batch.set(docRef, newAttendance)
        }
        
        batch.commit().await()
    }
}
