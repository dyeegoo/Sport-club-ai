package com.sportclubai.domain.repository

import android.net.Uri
import com.sportclubai.domain.model.Student
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getStudentsByClub(clubId: String): Flow<List<Student>>
    suspend fun getStudentById(clubId: String, studentId: String): Student?
    suspend fun addStudent(clubId: String, student: Student, imageUri: Uri?): String
    suspend fun updateStudent(clubId: String, student: Student, imageUri: Uri?)
    suspend fun deleteStudent(clubId: String, studentId: String)
}
