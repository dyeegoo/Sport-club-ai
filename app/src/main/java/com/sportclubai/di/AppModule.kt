package com.sportclubai.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sportclubai.data.repository.AuthRepositoryImpl
import com.sportclubai.data.repository.ClubRepositoryImpl
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ClubRepository
import com.sportclubai.domain.repository.DashboardRepository
import com.sportclubai.data.repository.DashboardRepositoryImpl
import com.sportclubai.domain.repository.StudentRepository
import com.sportclubai.data.repository.StudentRepositoryImpl
import com.sportclubai.domain.repository.AttendanceRepository
import com.sportclubai.data.repository.AttendanceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.sportclubai.domain.repository.TrainingRepository
import com.sportclubai.data.repository.TrainingRepositoryImpl
import com.sportclubai.domain.repository.AITrainingRepository
import com.sportclubai.data.repository.AITrainingRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): AuthRepository {
        return AuthRepositoryImpl(auth, firestore, storage)
    }

    @Provides
    @Singleton
    fun provideClubRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): ClubRepository {
        return ClubRepositoryImpl(firestore, storage)
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(
        firestore: FirebaseFirestore
    ): DashboardRepository {
        return DashboardRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideStudentRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): StudentRepository {
        return StudentRepositoryImpl(firestore, storage)
    }

    @Provides
    @Singleton
    fun provideAttendanceRepository(
        firestore: FirebaseFirestore
    ): AttendanceRepository {
        return AttendanceRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.PaymentRepository {
        return com.sportclubai.data.repository.PaymentRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideCoachRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.CoachRepository {
        return com.sportclubai.data.repository.CoachRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideClassRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.ClassRepository {
        return com.sportclubai.data.repository.ClassRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.AnalyticsRepository {
        return com.sportclubai.data.repository.AnalyticsRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.MessageRepository {
        return com.sportclubai.data.repository.MessageRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.NotificationRepository {
        return com.sportclubai.data.repository.NotificationRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideCloudFunctionManager(): com.sportclubai.domain.repository.CloudFunctionManager {
        return com.sportclubai.data.remote.CloudFunctionManagerImpl()
    }

    @Provides
    @Singleton
    fun provideFcmManager(): com.sportclubai.domain.repository.FcmManager {
        return com.sportclubai.data.remote.FcmManagerImpl()
    }

    @Provides
    @Singleton
    fun provideTrainingRepository(
        firestore: FirebaseFirestore
    ): TrainingRepository {
        return TrainingRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAITrainingRepository(): AITrainingRepository {
        return AITrainingRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideExamRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.ExamRepository {
        return com.sportclubai.data.repository.ExamRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideSubscriptionRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.SubscriptionRepository {
        return com.sportclubai.data.repository.SubscriptionRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideBillingRepository(): com.sportclubai.domain.repository.BillingRepository {
        return com.sportclubai.data.repository.BillingRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideWhiteLabelRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.WhiteLabelRepository {
        return com.sportclubai.data.repository.WhiteLabelRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideCloudFunctionsRepository(): com.sportclubai.domain.repository.CloudFunctionsRepository {
        return com.sportclubai.data.repository.CloudFunctionsRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideSecurityRepository(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): com.sportclubai.domain.repository.SecurityRepository {
        return com.sportclubai.data.repository.SecurityRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideAuditLogRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.AuditLogRepository {
        return com.sportclubai.data.repository.AuditLogRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideBackupRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.BackupRepository {
        return com.sportclubai.data.repository.BackupRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideMonitoringRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.MonitoringRepository {
        return com.sportclubai.data.repository.MonitoringRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideExportRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.ExportRepository {
        return com.sportclubai.data.repository.ExportRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        firestore: FirebaseFirestore
    ): com.sportclubai.domain.repository.SearchRepository {
        return com.sportclubai.data.repository.SearchRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): com.google.firebase.analytics.FirebaseAnalytics {
        return com.google.firebase.analytics.FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAppAnalytics(
        firebaseAnalytics: com.google.firebase.analytics.FirebaseAnalytics
    ): com.sportclubai.core.analytics.AppAnalytics {
        return com.sportclubai.core.analytics.AppAnalytics(firebaseAnalytics)
    }

    @Provides
    @Singleton
    fun provideCrashReporter(): com.sportclubai.core.crash.CrashReporter {
        return com.sportclubai.core.crash.CrashReporter()
    }
}
