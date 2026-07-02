package com.sportclubai.domain.repository

import com.sportclubai.domain.model.Exam
import com.sportclubai.domain.model.ExamStudent
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    fun getExamsForClub(clubId: String): Flow<List<Exam>>
    fun getUpcomingExams(clubId: String): Flow<List<Exam>>
    suspend fun getExamById(examId: String): Exam?
    suspend fun createOrUpdateExam(exam: Exam)
    suspend fun deleteExam(examId: String)
    suspend fun updateStudentEvaluation(examId: String, student: ExamStudent)
}
