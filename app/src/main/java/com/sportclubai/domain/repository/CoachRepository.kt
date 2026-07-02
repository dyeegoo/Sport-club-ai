package com.sportclubai.domain.repository

import com.sportclubai.domain.model.Coach
import kotlinx.coroutines.flow.Flow

interface CoachRepository {
    fun getCoachesByClub(clubId: String): Flow<List<Coach>>
    suspend fun getCoachById(clubId: String, coachId: String): Coach?
    suspend fun addCoach(clubId: String, coach: Coach)
    suspend fun updateCoach(clubId: String, coach: Coach)
    suspend fun deleteCoach(clubId: String, coachId: String)
}
