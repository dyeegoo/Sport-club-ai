package com.sportclubai.domain.usecase

import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.CoachRepository
import javax.inject.Inject

class DeleteCoachUseCase @Inject constructor(
    private val coachRepository: CoachRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(coachId: String) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner") {
            throw Exception("Unauthorized: Only owners can delete coaches")
        }
        
        coachRepository.deleteCoach(uid, coachId)
    }
}
