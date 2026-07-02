package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.CoachRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoachesUseCase @Inject constructor(
    private val coachRepository: CoachRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<List<Coach>> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        return coachRepository.getCoachesByClub(uid)
    }
}
