package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.AttendanceAnalytics
import com.sportclubai.domain.repository.AnalyticsRepository
import com.sportclubai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttendanceAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<AttendanceAnalytics> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User data not found")
        return analyticsRepository.getAttendanceAnalytics(user.clubId)
    }
}
