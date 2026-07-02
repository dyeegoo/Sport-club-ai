package com.sportclubai.domain.model

data class BeltRequirement(
    val beltColor: String = "",
    val minimumAttendance: Double = 0.0,
    val requiredSkills: List<String> = emptyList()
)

data class ExamCriteria(
    val name: String = "",
    val description: String = "",
    val maxScore: Double = 10.0,
    val minPassScore: Double = 5.0
)

data class ExamResult(
    val criteriaName: String = "",
    val score: Double = 0.0,
    val notes: String = ""
)

enum class ExamStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
}

data class ExamStudent(
    val studentId: String = "",
    val studentName: String = "",
    val currentBelt: String = "",
    val targetBelt: String = "",
    val results: List<ExamResult> = emptyList(),
    val totalScore: Double = 0.0,
    val passed: Boolean = false,
    val evaluatorId: String = "",
    val certificateNumber: String? = null,
    val issueDate: Long? = null
)

data class Exam(
    val id: String = "",
    val clubId: String = "",
    val title: String = "",
    val date: Long = 0L,
    val sportType: String = "",
    val targetBelt: String = "",
    val criteria: List<ExamCriteria> = emptyList(),
    val students: List<ExamStudent> = emptyList(),
    val assignedCoachIds: List<String> = emptyList(),
    val status: ExamStatus = ExamStatus.SCHEDULED,
    val createdAt: Long = System.currentTimeMillis()
)
