package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Attendance
import com.sportclubai.domain.repository.AttendanceRepository
import com.sportclubai.domain.repository.AuthRepository
import javax.inject.Inject

class MarkBulkAttendanceUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(attendances: List<Attendance>) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner" && user.role != "coach") {
            throw Exception("Unauthorized access")
        }
        
        val newAttendances = attendances.map { it.copy(markedBy = uid) }
        attendanceRepository.markBulkAttendance(uid, newAttendances)
    }
}
