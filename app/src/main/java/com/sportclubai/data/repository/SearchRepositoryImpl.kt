package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.SearchResult
import com.sportclubai.domain.repository.SearchRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SearchRepository {

    override suspend fun search(clubId: String, query: String, typeFilter: String?): List<SearchResult> {
        // Simple placeholder search. In a real app, use Algolia or Typesense for full-text search
        val results = mutableListOf<SearchResult>()
        if (query.isEmpty()) return results
        
        val q = query.lowercase()

        if (typeFilter == null || typeFilter == "student") {
            val snapshot = firestore.collection("clubs").document(clubId).collection("students").get().await()
            snapshot.documents.forEach { doc ->
                val name = doc.getString("fullName") ?: ""
                if (name.lowercase().contains(q)) {
                    results.add(SearchResult(doc.id, name, "Student", "student"))
                }
            }
        }
        
        // Add more collections as needed
        return results
    }

    override suspend fun getRecentSearches(clubId: String): List<String> {
        val snapshot = firestore.collection("clubs").document(clubId).collection("recent_searches")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get().await()
        return snapshot.documents.mapNotNull { it.getString("query") }
    }

    override suspend fun saveRecentSearch(clubId: String, query: String) {
        firestore.collection("clubs").document(clubId).collection("recent_searches").add(
            mapOf("query" to query, "timestamp" to System.currentTimeMillis())
        ).await()
    }
}
