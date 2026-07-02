package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Coach
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.CoachRepository
import javax.inject.Inject

class UpdateCoachUseCase @Inject constructor(
    private val coachRepository: CoachRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(coach: Coach) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner") {
            throw Exception("Unauthorized: Only owners can update coaches")
        }
        
        coachRepository.updateCoach(uid, coach)
    }
}
