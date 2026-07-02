package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.TrainingPlan
import com.sportclubai.domain.repository.AITrainingRepository
import com.sportclubai.domain.repository.TrainingRepository
import javax.inject.Inject

class GenerateAITrainingPlanUseCase @Inject constructor(
    private val aiTrainingRepository: AITrainingRepository,
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(
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
    ): TrainingPlan {
        val generatedPlan = aiTrainingRepository.generateTrainingPlan(
            studentId = studentId,
            sportType = sportType,
            currentBelt = currentBelt,
            age = age,
            attendancePercentage = attendancePercentage,
            weakSkills = weakSkills,
            strongSkills = strongSkills,
            weeklyAvailabilityDays = weeklyAvailabilityDays,
            targetExamDate = targetExamDate,
            coachNotes = coachNotes
        )
        // Optionally save the generated plan directly to the DB, or return it for the user to confirm/save.
        return generatedPlan
    }
}
