package com.sportclubai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sportclubai.domain.model.Payment
import com.sportclubai.domain.repository.PaymentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PaymentRepository {

    override fun getPaymentsByStudent(clubId: String, studentId: String): Flow<List<Payment>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId)
            .collection("payments")
            .whereEqualTo("studentId", studentId)
            
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val payments = snapshot?.documents?.mapNotNull { it.toObject(Payment::class.java) } ?: emptyList()
            trySend(payments.sortedByDescending { it.dueDate })
        }
        awaitClose { listener.remove() }
    }

    override fun getClubPayments(clubId: String): Flow<List<Payment>> = callbackFlow {
        val collection = firestore.collection("clubs").document(clubId)
            .collection("payments")
            
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val payments = snapshot?.documents?.mapNotNull { it.toObject(Payment::class.java) } ?: emptyList()
            trySend(payments.sortedByDescending { it.dueDate })
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createPayment(clubId: String, payment: Payment) {
        val collection = firestore.collection("clubs").document(clubId).collection("payments")
        val docRef = if (payment.paymentId.isEmpty()) collection.document() else collection.document(payment.paymentId)
        val newPayment = if (payment.paymentId.isEmpty()) payment.copy(paymentId = docRef.id) else payment
        docRef.set(newPayment).await()
    }

    override suspend fun updatePaymentStatus(clubId: String, paymentId: String, status: String, paidDate: String) {
        val updates = mutableMapOf<String, Any>(
            "status" to status
        )
        if (paidDate.isNotEmpty()) {
            updates["paidDate"] = paidDate
        }
        
        firestore.collection("clubs").document(clubId)
            .collection("payments").document(paymentId)
            .update(updates).await()
    }
}
