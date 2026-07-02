package com.sportclubai.domain.model

data class Attendance(
    val attendanceId: String = "",
    val clubId: String = "",
    val studentId: String = "",
    val classId: String = "",
    val date: String = "", // YYYY-MM-DD
    val status: String = "PRESENT", // PRESENT, ABSENT, LATE, EXCUSED
    val checkInTime: Long = 0L,
    val checkOutTime: Long = 0L,
    val markedBy: String = "",
    val notes: String = ""
)
