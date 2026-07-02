package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.StudentPerformanceStats
import com.sportclubai.domain.repository.AnalyticsRepository
import com.sportclubai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStudentPerformanceStatsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<List<StudentPerformanceStats>> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User data not found")
        return analyticsRepository.getStudentPerformanceStats(user.clubId)
    }
}
