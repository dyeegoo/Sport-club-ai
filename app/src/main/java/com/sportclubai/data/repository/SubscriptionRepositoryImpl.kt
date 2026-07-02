package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.*
import com.sportclubai.domain.repository.SubscriptionRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SubscriptionRepository {

    override fun getSubscription(clubId: String): Flow<Subscription?> = callbackFlow {
        val listener = firestore.collection("subscriptions").document(clubId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(Subscription::class.java))
                } else {
                    // Default to FREE if not exists
                    trySend(Subscription(clubId = clubId, plan = SubscriptionPlan.FREE))
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getUsageStats(clubId: String): Flow<UsageStats?> = callbackFlow {
        val listener = firestore.collection("usage_stats").document(clubId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(UsageStats::class.java))
                } else {
                    trySend(UsageStats(clubId = clubId))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getInvoices(clubId: String): List<Invoice> {
        val snapshot = firestore.collection("invoices")
            .whereEqualTo("clubId", clubId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Invoice::class.java) }
    }

    override suspend fun applyCoupon(clubId: String, couponCode: String): Result<Coupon> {
        return try {
            val doc = firestore.collection("coupons").document(couponCode).get().await()
            if (doc.exists()) {
                val coupon = doc.toObject(Coupon::class.java)!!
                if (coupon.validUntil > System.currentTimeMillis()) {
                    Result.success(coupon)
                } else {
                    Result.failure(Exception("Coupon expired"))
                }
            } else {
                Result.failure(Exception("Invalid coupon code"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun upgradePlan(clubId: String, newPlan: SubscriptionPlan) {
        // In a real app, this would be triggered via a Cloud Function after successful payment.
        // For now, we mock the update.
        val ref = firestore.collection("subscriptions").document(clubId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val sub = snapshot.toObject(Subscription::class.java) ?: Subscription(clubId = clubId)
            val updated = sub.copy(plan = newPlan, isActive = true)
            transaction.set(ref, updated)
        }.await()
    }

    override suspend fun cancelSubscription(clubId: String) {
        firestore.collection("subscriptions").document(clubId)
            .update("cancelAtPeriodEnd", true)
            .await()
    }

    override suspend fun incrementUsage(clubId: String, metric: String, amount: Int) {
        // Handled by Cloud Functions primarily, but we can simulate here
        val ref = firestore.collection("usage_stats").document(clubId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            if (snapshot.exists()) {
                val current = snapshot.getLong(metric) ?: 0L
                transaction.update(ref, metric, current + amount)
            } else {
                val stats = UsageStats(clubId = clubId)
                transaction.set(ref, stats)
                transaction.update(ref, metric, amount.toLong())
            }
        }.await()
    }
}
