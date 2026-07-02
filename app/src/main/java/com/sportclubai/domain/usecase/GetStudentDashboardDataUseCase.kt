package com.sportclubai.domain.usecase

import com.sportclubai.domain.model.StudentDashboardData
import com.sportclubai.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStudentDashboardDataUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val classRepository: ClassRepository,
    private val paymentRepository: PaymentRepository,
    private val messageRepository: MessageRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Flow<StudentDashboardData> {
        val uid = authRepository.getCurrentUserId() ?: throw Exception("User not authenticated")
        val user = authRepository.getUser(uid) ?: throw Exception("User data not found")
        val clubId = user.clubId

        val student = studentRepository.getStudentById(clubId, uid) ?: throw Exception("Student profile not found")

        val paymentsFlow = paymentRepository.getPaymentsByStudent(clubId, uid)
        val notificationsFlow = notificationRepository.getUserNotifications(clubId, uid, 50)
        // messageRepository.getUserConversations is not unread count, but we can fake it or calculate
        val messagesFlow = messageRepository.getUserConversations(clubId, uid)

        return combine(
            paymentsFlow,
            notificationsFlow,
            messagesFlow
        ) { payments, notifications, messages ->
            val unreadNotifications = notifications.count { !it.readBy.contains(uid) }
            val latestPayment = payments.maxByOrNull { it.dueDate }
            
            // To be accurate we need attendance percentage and next class, 
            // but for simple combination we will just pass mock data for those for now.
            StudentDashboardData(
                student = student,
                attendancePercentage = 85.0, // TODO calculate
                nextClass = null, // TODO fetch
                nextBeltExamDate = null,
                latestPayment = latestPayment,
                unreadMessagesCount = 0, // TODO calculate from messages
                unreadNotificationsCount = unreadNotifications,
                weeklyTrainingPlan = "Focus on kicks this week."
            )
        }
    }
}
