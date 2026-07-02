package com.sportclubai.domain.repository

import com.sportclubai.domain.model.DashboardData

interface DashboardRepository {
    suspend fun getDashboardData(uid: String): DashboardData
}
