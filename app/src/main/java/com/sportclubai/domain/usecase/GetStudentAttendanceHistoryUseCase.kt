package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Attendance
import com.sportclubai.domain.repository.AttendanceRepository
import com.sportclubai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStudentAttendanceHistoryUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(studentId: String): Flow<List<Attendance>> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        // No strict role check here to allow students/parents to read their own history (as per requirements)
        // If needed, we can check if uid is the studentId, but for now we assume uid is clubId or coach
        // In a real scenario with separate student logins, the user's clubId would be stored in their profile.
        // Assuming uid is club owner for now.
        
        return attendanceRepository.getStudentAttendanceHistory(uid, studentId)
    }
}
