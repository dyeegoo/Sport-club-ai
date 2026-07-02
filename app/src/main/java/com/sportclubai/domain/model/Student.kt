package com.sportclubai.domain.model

data class Student(
    val studentId: String = "",
    val clubId: String = "",
    val fullName: String = "",
    val profileImage: String = "",
    val phoneNumber: String = "",
    val parentName: String = "",
    val parentPhone: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val height: Float = 0f,
    val weight: Float = 0f,
    val beltLevel: String = "White",
    val registrationDate: Long = System.currentTimeMillis(),
    val membershipStatus: String = "Active",
    val notes: String = "",
    val attendanceCount: Int = 0,
    val absenceCount: Int = 0
)
