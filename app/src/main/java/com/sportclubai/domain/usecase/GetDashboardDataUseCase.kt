package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.DashboardData
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.DashboardRepository
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): DashboardData {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner") {
            throw Exception("Unauthorized: Only owners can access the dashboard")
        }
        
        return dashboardRepository.getDashboardData(uid)
    }
}
