package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.User
import com.sportclubai.domain.repository.AuthRepository
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        val uid = authRepository.getCurrentUserId()
        return if (uid != null) {
            authRepository.getUser(uid)
        } else {
            null
        }
    }
}
