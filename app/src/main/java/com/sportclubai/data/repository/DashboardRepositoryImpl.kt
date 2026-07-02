package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.DashboardData
import com.sportclubai.domain.repository.DashboardRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DashboardRepository {
    override suspend fun getDashboardData(uid: String): DashboardData {
        // Find club by ownerId
        val clubsSnapshot = firestore.collection("clubs")
            .whereEqualTo("ownerId", uid)
            .limit(1)
            .get()
            .await()
            
        val clubName = if (!clubsSnapshot.isEmpty) {
            clubsSnapshot.documents[0].getString("name") ?: "My Club"
        } else {
            "My Club"
        }

        // Return mostly stubbed aggregated data for now
        return DashboardData(
            clubName = clubName,
            totalStudents = 0,
            totalCoaches = 0,
            activeClassesToday = 0,
            attendanceSummary = "0 / 0 Present",
            monthlyRevenue = 0.0,
            upcomingBeltExams = 0,
            notificationsCount = 0
        )
    }
}
