package com.sportclubai.domain.model

data class DashboardData(
    val clubName: String,
    val totalStudents: Int,
    val totalCoaches: Int,
    val activeClassesToday: Int,
    val attendanceSummary: String,
    val monthlyRevenue: Double,
    val upcomingBeltExams: Int,
    val notificationsCount: Int
)
