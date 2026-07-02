package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.*
import com.sportclubai.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AnalyticsRepository {

    override fun getClubOverviewStats(clubId: String): Flow<ClubOverviewStats> = flow {
        val students = firestore.collection("clubs").document(clubId).collection("students").get().await()
        val classes = firestore.collection("clubs").document(clubId).collection("classes").get().await()
        val payments = firestore.collection("clubs").document(clubId).collection("payments").get().await()
        
        val totalStudents = students.size()
        val activeClasses = classes.documents.count { it.getString("status") == "ACTIVE" }
        var totalRevenue = 0.0
        payments.documents.forEach {
            if (it.getString("status") == "PAID") {
                totalRevenue += it.getDouble("amount") ?: 0.0
            }
        }
        
        emit(ClubOverviewStats(totalStudents, activeClasses, totalRevenue, 0.0))
    }

    override fun getStudentPerformanceStats(clubId: String): Flow<List<StudentPerformanceStats>> = flow {
        val students = firestore.collection("clubs").document(clubId).collection("students").get().await()
        val stats = students.documents.map {
            StudentPerformanceStats(
                studentId = it.id,
                studentName = it.getString("fullName") ?: "",
                attendancePercentage = 0.0, // Simplified for now
                paymentCompliance = true
            )
        }
        emit(stats)
    }

    override fun getAttendanceAnalytics(clubId: String): Flow<AttendanceAnalytics> = flow {
        emit(AttendanceAnalytics(emptyMap(), emptyMap()))
    }

    override fun getFinancialAnalytics(clubId: String): Flow<FinancialAnalytics> = flow {
        val payments = firestore.collection("clubs").document(clubId).collection("payments").get().await()
        val total = payments.size()
        var paid = 0
        var pending = 0
        payments.documents.forEach {
            if (it.getString("status") == "PAID") paid++ else pending++
        }
        val rate = if (total > 0) paid.toDouble() / total else 0.0
        emit(FinancialAnalytics(emptyMap(), pending, rate))
    }

    override fun getCoachStats(clubId: String): Flow<List<CoachStats>> = flow {
        val coaches = firestore.collection("clubs").document(clubId).collection("coaches").get().await()
        val classes = firestore.collection("clubs").document(clubId).collection("classes").get().await()
        
        val stats = coaches.documents.map { doc ->
            val coachId = doc.id
            val coachClasses = classes.documents.filter { it.getString("coachId") == coachId }
            val classCount = coachClasses.size
            var studentCount = 0
            coachClasses.forEach { c ->
                val studentsList = c.get("assignedStudents") as? List<*>
                studentCount += studentsList?.size ?: 0
            }
            CoachStats(
                coachId = coachId,
                coachName = doc.getString("fullName") ?: "",
                studentCount = studentCount,
                classCount = classCount
            )
        }
        emit(stats)
    }
}
