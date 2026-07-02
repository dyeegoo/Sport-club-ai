package com.sportclubai.domain.repository

import com.sportclubai.domain.model.SearchResult

interface SearchRepository {
    suspend fun search(clubId: String, query: String, typeFilter: String? = null): List<SearchResult>
    suspend fun getRecentSearches(clubId: String): List<String>
    suspend fun saveRecentSearch(clubId: String, query: String)
}
