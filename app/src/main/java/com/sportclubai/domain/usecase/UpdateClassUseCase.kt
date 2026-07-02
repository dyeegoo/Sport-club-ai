package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.SportClass
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ClassRepository
import javax.inject.Inject

class UpdateClassUseCase @Inject constructor(
    private val classRepository: ClassRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(sportClass: SportClass) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner" && user.role != "coach") {
            throw Exception("Unauthorized: Only owners and coaches can update classes")
        }
        
        classRepository.updateClass(uid, sportClass)
    }
}
