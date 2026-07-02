package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.SportClass
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ClassRepository
import javax.inject.Inject

class GetClassByIdUseCase @Inject constructor(
    private val classRepository: ClassRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(classId: String): SportClass? {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        return classRepository.getClassById(uid, classId)
    }
}
