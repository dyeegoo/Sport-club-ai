package com.sportclubai.domain.model

data class SearchResult(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: String // "student", "coach", "class", "payment", "attendance", "message"
)
