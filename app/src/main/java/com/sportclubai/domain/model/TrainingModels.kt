package com.sportclubai.domain.model

enum class TrainingLevel {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
}

enum class ExerciseCategory {
    WARM_UP, STRETCHING, TECHNIQUE, SPARRING, FITNESS, COOLDOWN
}

data class Exercise(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val durationMinutes: Int = 0,
    val category: ExerciseCategory = ExerciseCategory.TECHNIQUE,
    val difficulty: Int = 1,
    val isCompleted: Boolean = false
)

data class TrainingDay(
    val dayOfWeek: String = "",
    val exercises: List<Exercise> = emptyList(),
    val totalDurationMinutes: Int = 0,
    val isCompleted: Boolean = false
)

data class TrainingWeek(
    val weekNumber: Int = 1,
    val days: List<TrainingDay> = emptyList(),
    val focus: String = ""
)

data class TrainingPlan(
    val id: String = "",
    val studentId: String = "",
    val coachId: String = "",
    val title: String = "",
    val sportType: String = "",
    val beltLevel: String = "",
    val targetExamDate: Long? = null,
    val weeks: List<TrainingWeek> = emptyList(),
    val difficulty: TrainingLevel = TrainingLevel.BEGINNER,
    val createdAt: Long = System.currentTimeMillis(),
    val coachNotes: String = ""
)
