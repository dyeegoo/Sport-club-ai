package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.User
import com.sportclubai.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): User {
        val result = authRepository.login(email, password)
        val user = authRepository.getUser(result.uid)
        return user ?: throw Exception("User data not found")
    }
}
