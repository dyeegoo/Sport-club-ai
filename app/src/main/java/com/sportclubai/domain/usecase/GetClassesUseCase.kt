package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.SportClass
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ClassRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetClassesUseCase @Inject constructor(
    private val classRepository: ClassRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<List<SportClass>> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        return classRepository.getClassesByClub(uid)
    }
}
