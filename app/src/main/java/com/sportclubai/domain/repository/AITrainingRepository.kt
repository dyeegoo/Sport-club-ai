package com.sportclubai.domain.repository

import com.sportclubai.domain.model.TrainingPlan

interface AITrainingRepository {
    suspend fun generateTrainingPlan(
        studentId: String,
        sportType: String,
        currentBelt: String,
        age: Int,
        attendancePercentage: Double,
        weakSkills: List<String>,
        strongSkills: List<String>,
        weeklyAvailabilityDays: Int,
        targetExamDate: Long?,
        coachNotes: String
    ): TrainingPlan
}
