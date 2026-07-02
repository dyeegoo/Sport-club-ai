package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.CoachRepository
import javax.inject.Inject

class GetCoachByIdUseCase @Inject constructor(
    private val coachRepository: CoachRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(coachId: String): Coach? {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        return coachRepository.getCoachById(uid, coachId)
    }
}
