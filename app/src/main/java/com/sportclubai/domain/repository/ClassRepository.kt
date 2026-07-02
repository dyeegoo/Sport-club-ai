package com.sportclubai.domain.repository

import com.sportclubai.domain.model.SportClass
import kotlinx.coroutines.flow.Flow

interface ClassRepository {
    fun getClassesByClub(clubId: String): Flow<List<SportClass>>
    suspend fun getClassById(clubId: String, classId: String): SportClass?
    suspend fun createClass(clubId: String, sportClass: SportClass)
    suspend fun updateClass(clubId: String, sportClass: SportClass)
    suspend fun addStudentToClass(clubId: String, classId: String, studentId: String)
    suspend fun removeStudentFromClass(clubId: String, classId: String, studentId: String)
}
