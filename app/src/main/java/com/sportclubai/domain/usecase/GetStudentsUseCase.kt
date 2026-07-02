package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.Student
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStudentsUseCase @Inject constructor(
    private val studentRepository: StudentRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<List<Student>> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner" && user.role != "coach") {
            throw Exception("Unauthorized: Only owners and coaches can view students")
        }
        
        return studentRepository.getStudentsByClub(uid)
    }
}
