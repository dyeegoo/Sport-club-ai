package com.sportclubai.domain.model

data class Club(
    val clubId: String = "",
    val name: String = "",
    val logoUrl: String = "",
    val sportType: String = "",
    val country: String = "",
    val city: String = "",
    val address: String = "",
    val phone: String = "",
    val website: String = "",
    val language: String = "en",
    val currency: String = "USD",
    val timeZone: String = "UTC",
    val ownerId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
