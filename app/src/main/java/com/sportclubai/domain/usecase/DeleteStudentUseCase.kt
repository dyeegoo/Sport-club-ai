package com.sportclubai.domain.usecase

import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.StudentRepository
import javax.inject.Inject

class DeleteStudentUseCase @Inject constructor(
    private val studentRepository: StudentRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(studentId: String) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner" && user.role != "coach") {
            throw Exception("Unauthorized: Only owners and coaches can delete students")
        }
        
        studentRepository.deleteStudent(uid, studentId)
    }
}
