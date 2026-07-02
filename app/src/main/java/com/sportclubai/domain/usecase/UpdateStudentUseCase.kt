package com.sportclubai.domain.usecase

import android.net.Uri
import com.sportclubai.domain.model.Student
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.StudentRepository
import javax.inject.Inject

class UpdateStudentUseCase @Inject constructor(
    private val studentRepository: StudentRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(student: Student, imageUri: Uri?) {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User not found")
        
        if (user.role != "owner" && user.role != "coach") {
            throw Exception("Unauthorized: Only owners and coaches can update students")
        }
        
        studentRepository.updateStudent(uid, student, imageUri)
    }
}
