package com.sportclubai.domain.repository

import com.sportclubai.domain.model.TrainingPlan
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    fun getTrainingPlansForStudent(studentId: String): Flow<List<TrainingPlan>>
    suspend fun getTrainingPlan(planId: String): TrainingPlan?
    suspend fun saveTrainingPlan(plan: TrainingPlan)
    suspend fun markExerciseCompleted(planId: String, weekNumber: Int, dayOfWeek: String, exerciseId: String, completed: Boolean)
}
