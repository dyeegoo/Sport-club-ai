package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.TrainingPlan
import com.sportclubai.domain.repository.TrainingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TrainingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TrainingRepository {

    override fun getTrainingPlansForStudent(studentId: String): Flow<List<TrainingPlan>> = callbackFlow {
        val listener = firestore.collection("training_plans")
            .whereEqualTo("studentId", studentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val plans = snapshot.documents.mapNotNull { it.toObject(TrainingPlan::class.java) }
                    trySend(plans.sortedByDescending { it.createdAt })
                } else {
                    trySend(emptyList())
                }
            }
            
        awaitClose { listener.remove() }
    }

    override suspend fun getTrainingPlan(planId: String): TrainingPlan? {
        val doc = firestore.collection("training_plans").document(planId).get().await()
        return doc.toObject(TrainingPlan::class.java)
    }

    override suspend fun saveTrainingPlan(plan: TrainingPlan) {
        val id = plan.id.ifEmpty { firestore.collection("training_plans").document().id }
        val finalPlan = plan.copy(id = id)
        firestore.collection("training_plans").document(id).set(finalPlan).await()
    }

    override suspend fun markExerciseCompleted(
        planId: String,
        weekNumber: Int,
        dayOfWeek: String,
        exerciseId: String,
        completed: Boolean
    ) {
        firestore.runTransaction { transaction ->
            val ref = firestore.collection("training_plans").document(planId)
            val snapshot = transaction.get(ref)
            val plan = snapshot.toObject(TrainingPlan::class.java) ?: return@runTransaction
            
            // Need to update deeply nested fields. 
            // The simplest way in Kotlin without a complex map structure is updating the entire object.
            val updatedWeeks = plan.weeks.map { week ->
                if (week.weekNumber == weekNumber) {
                    val updatedDays = week.days.map { day ->
                        if (day.dayOfWeek == dayOfWeek) {
                            val updatedExercises = day.exercises.map { ex ->
                                if (ex.id == exerciseId) ex.copy(isCompleted = completed) else ex
                            }
                            // check if all exercises in day are completed
                            val allCompleted = updatedExercises.all { it.isCompleted }
                            day.copy(exercises = updatedExercises, isCompleted = allCompleted)
                        } else day
                    }
                    week.copy(days = updatedDays)
                } else week
            }
            
            transaction.set(ref, plan.copy(weeks = updatedWeeks))
        }.await()
    }
}
