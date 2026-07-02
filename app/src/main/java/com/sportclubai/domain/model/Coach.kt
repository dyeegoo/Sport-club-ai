package com.sportclubai.domain.model

data class Coach(
    val coachId: String = "",
    val clubId: String = "",
    val fullName: String = "",
    val profileImage: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val specialization: String = "",
    val assignedClasses: List<String> = emptyList(),
    val status: String = "ACTIVE",
    val joinDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)
