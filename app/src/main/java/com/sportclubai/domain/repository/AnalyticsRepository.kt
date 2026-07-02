package com.sportclubai.domain.repository

import com.sportclubai.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getClubOverviewStats(clubId: String): Flow<ClubOverviewStats>
    fun getStudentPerformanceStats(clubId: String): Flow<List<StudentPerformanceStats>>
    fun getAttendanceAnalytics(clubId: String): Flow<AttendanceAnalytics>
    fun getFinancialAnalytics(clubId: String): Flow<FinancialAnalytics>
    fun getCoachStats(clubId: String): Flow<List<CoachStats>>
}
