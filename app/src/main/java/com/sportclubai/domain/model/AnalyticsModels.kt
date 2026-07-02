package com.sportclubai.domain.model

data class ClubOverviewStats(
    val totalStudents: Int = 0,
    val activeClasses: Int = 0,
    val totalRevenue: Double = 0.0,
    val averageAttendanceRate: Double = 0.0
)

data class StudentPerformanceStats(
    val studentId: String,
    val studentName: String,
    val attendancePercentage: Double,
    val paymentCompliance: Boolean
)

data class AttendanceAnalytics(
    val weeklyTrends: Map<String, Int>,
    val classComparison: Map<String, Double>
)

data class FinancialAnalytics(
    val monthlyRevenue: Map<String, Double>,
    val outstandingPaymentsCount: Int,
    val completionRate: Double
)

data class CoachStats(
    val coachId: String,
    val coachName: String,
    val studentCount: Int,
    val classCount: Int
)
