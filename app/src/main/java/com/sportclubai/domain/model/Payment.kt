package com.sportclubai.domain.model

data class Payment(
    val paymentId: String = "",
    val clubId: String = "",
    val studentId: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val paymentType: String = "MONTHLY_FEE", // MONTHLY_FEE, REGISTRATION_FEE, PENALTY, OTHER
    val status: String = "PENDING", // PAID, PENDING, OVERDUE
    val dueDate: String = "", // YYYY-MM-DD
    val paidDate: String = "", // YYYY-MM-DD
    val createdBy: String = "",
    val notes: String = ""
)
