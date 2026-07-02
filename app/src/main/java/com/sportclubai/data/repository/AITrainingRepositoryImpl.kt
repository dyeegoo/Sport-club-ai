package com.sportclubai.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.sportclubai.domain.model.Exercise
import com.sportclubai.domain.model.ExerciseCategory
import com.sportclubai.domain.model.TrainingDay
import com.sportclubai.domain.model.TrainingLevel
import com.sportclubai.domain.model.TrainingPlan
import com.sportclubai.domain.model.TrainingWeek
import com.sportclubai.domain.repository.AITrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

class AITrainingRepositoryImpl @Inject constructor() : AITrainingRepository {

    // Ideally the API key should come from a secure source or BuildConfig, 
    // but for the sake of this prototype/MVP, we require it to be set or we mock if empty.
    private val apiKey = "YOUR_API_KEY_HERE" // Replace with real key or build config

    override suspend fun generateTrainingPlan(
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
    ): TrainingPlan = withContext(Dispatchers.IO) {
        if (apiKey == "YOUR_API_KEY_HERE" || apiKey.isBlank()) {
            return@withContext generateMockTrainingPlan(studentId, sportType, currentBelt)
        }

        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey
        )

        val prompt = """
            You are a professional martial arts coach. Generate a weekly training plan for a student in JSON format.
            Student Profile:
            Sport: ${sportType}
            Belt Level: ${currentBelt}
            Age: ${age}
            Attendance: ${attendancePercentage}%
            Weak Skills: ${weakSkills.joinToString()}
            Strong Skills: ${strongSkills.joinToString()}
            Availability: ${weeklyAvailabilityDays} days per week
            Coach Notes: ${coachNotes}
            
            Return exactly a valid JSON object matching this structure without any markdown formatting:
            {
              "title": "String",
              "difficulty": "BEGINNER", // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
              "weeks": [
                {
                  "weekNumber": 1,
                  "focus": "String",
                  "days": [
                    {
                      "dayOfWeek": "Monday",
                      "exercises": [
                        {
                          "name": "String",
                          "description": "String",
                          "durationMinutes": 15,
                          "category": "WARM_UP", // WARM_UP, STRETCHING, TECHNIQUE, SPARRING, FITNESS, COOLDOWN
                          "difficulty": 2 // 1 to 5
                        }
                      ]
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(
                content { text(prompt) }
            )
            
            val jsonText = response.text?.replace("```json", "")?.replace("```", "")?.trim() 
                ?: throw Exception("No response from AI")
            
            val jsonObject = JSONObject(jsonText)
            
            val title = jsonObject.getString("title")
            val difficultyStr = jsonObject.optString("difficulty", "BEGINNER")
            val difficulty = runCatching { TrainingLevel.valueOf(difficultyStr) }.getOrDefault(TrainingLevel.BEGINNER)
            
            val weeksArray = jsonObject.getJSONArray("weeks")
            val weeks = mutableListOf<TrainingWeek>()
            
            for (i in 0 until weeksArray.length()) {
                val weekObj = weeksArray.getJSONObject(i)
                val daysArray = weekObj.getJSONArray("days")
                val days = mutableListOf<TrainingDay>()
                
                for (j in 0 until daysArray.length()) {
                    val dayObj = daysArray.getJSONObject(j)
                    val exercisesArray = dayObj.getJSONArray("exercises")
                    val exercises = mutableListOf<Exercise>()
                    var dayDuration = 0
                    
                    for (k in 0 until exercisesArray.length()) {
                        val exObj = exercisesArray.getJSONObject(k)
                        val duration = exObj.getInt("durationMinutes")
                        dayDuration += duration
                        exercises.add(
                            Exercise(
                                id = UUID.randomUUID().toString(),
                                name = exObj.getString("name"),
                                description = exObj.getString("description"),
                                durationMinutes = duration,
                                category = runCatching { ExerciseCategory.valueOf(exObj.getString("category")) }.getOrDefault(ExerciseCategory.TECHNIQUE),
                                difficulty = exObj.getInt("difficulty"),
                                isCompleted = false
                            )
                        )
                    }
                    
                    days.add(
                        TrainingDay(
                            dayOfWeek = dayObj.getString("dayOfWeek"),
                            exercises = exercises,
                            totalDurationMinutes = dayDuration,
                            isCompleted = false
                        )
                    )
                }
                
                weeks.add(
                    TrainingWeek(
                        weekNumber = weekObj.getInt("weekNumber"),
                        days = days,
                        focus = weekObj.optString("focus", "")
                    )
                )
            }
            
            TrainingPlan(
                id = UUID.randomUUID().toString(),
                studentId = studentId,
                title = title,
                sportType = sportType,
                beltLevel = currentBelt,
                targetExamDate = targetExamDate,
                weeks = weeks,
                difficulty = difficulty,
                coachNotes = "AI Generated Plan based on coach notes: $coachNotes"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to mock on parsing failure
            generateMockTrainingPlan(studentId, sportType, currentBelt)
        }
    }
    
    private fun generateMockTrainingPlan(studentId: String, sportType: String, belt: String): TrainingPlan {
        return TrainingPlan(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            title = "Mock $sportType Training ($belt)",
            sportType = sportType,
            beltLevel = belt,
            difficulty = TrainingLevel.INTERMEDIATE,
            weeks = listOf(
                TrainingWeek(
                    weekNumber = 1,
                    focus = "Fundamentals",
                    days = listOf(
                        TrainingDay(
                            dayOfWeek = "Monday",
                            totalDurationMinutes = 45,
                            exercises = listOf(
                                Exercise(id = UUID.randomUUID().toString(), name = "Jogging", durationMinutes = 10, category = ExerciseCategory.WARM_UP),
                                Exercise(id = UUID.randomUUID().toString(), name = "Forms", durationMinutes = 20, category = ExerciseCategory.TECHNIQUE),
                                Exercise(id = UUID.randomUUID().toString(), name = "Stretching", durationMinutes = 15, category = ExerciseCategory.COOLDOWN)
                            )
                        )
                    )
                )
            )
        )
    }
}
