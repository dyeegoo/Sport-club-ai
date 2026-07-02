package com.sportclubai.domain.repository

import com.sportclubai.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getAttendanceByDate(clubId: String, date: String): Flow<List<Attendance>>
    fun getStudentAttendanceHistory(clubId: String, studentId: String): Flow<List<Attendance>>
    fun getAttendanceSummary(clubId: String, startDate: String, endDate: String): Flow<List<Attendance>>
    suspend fun markAttendance(clubId: String, attendance: Attendance)
    suspend fun markBulkAttendance(clubId: String, attendances: List<Attendance>)
}
