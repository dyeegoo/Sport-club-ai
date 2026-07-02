package com.sportclubai.domain.model

data class StudentDashboardData(
    val student: Student = Student(),
    val attendancePercentage: Double = 0.0,
    val nextClass: SportClass? = null,
    val nextBeltExamDate: Long? = null,
    val latestPayment: Payment? = null,
    val unreadMessagesCount: Int = 0,
    val unreadNotificationsCount: Int = 0,
    val weeklyTrainingPlan: String = ""
)
