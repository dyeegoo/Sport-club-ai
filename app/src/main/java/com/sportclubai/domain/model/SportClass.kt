package com.sportclubai.domain.model

data class SportClass(
    val classId: String = "",
    val clubId: String = "",
    val className: String = "",
    val coachId: String = "",
    val scheduleDays: List<String> = emptyList(), // MON, TUE, WED, etc.
    val startTime: String = "",
    val endTime: String = "",
    val maxStudents: Int = 0,
    val assignedStudents: List<String> = emptyList(),
    val status: String = "ACTIVE", // ACTIVE, CANCELLED
    val location: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
