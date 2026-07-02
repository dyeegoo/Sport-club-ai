package com.sportclubai.core.navigation
import androidx.navigation.NavType
import androidx.navigation.navArgument

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sportclubai.presentation.splash.SplashScreen
import com.sportclubai.presentation.auth.LoginScreen
import com.sportclubai.presentation.club.RegisterClubScreen
import com.sportclubai.presentation.dashboard.DashboardScreen
import com.sportclubai.presentation.students.StudentsScreen
import com.sportclubai.presentation.studentprofile.StudentProfileScreen
import com.sportclubai.presentation.attendance.AttendanceScreen
import com.sportclubai.presentation.payments.PaymentsScreen
import com.sportclubai.presentation.aitraining.AITrainingScreen
import com.sportclubai.presentation.calendar.CalendarScreen
import com.sportclubai.presentation.notifications.NotificationsScreen
import com.sportclubai.presentation.reports.ReportsScreen
import com.sportclubai.presentation.settings.SettingsScreen
import com.sportclubai.presentation.coach.CoachProfileScreen
import com.sportclubai.presentation.parent.ParentPanelScreen
import com.sportclubai.presentation.competition.CompetitionScreen
import com.sportclubai.presentation.exams.BeltExamsScreen
import com.sportclubai.presentation.messages.MessagesScreen
import com.sportclubai.presentation.gallery.GalleryScreen
import com.sportclubai.presentation.documents.DocumentsScreen
import com.sportclubai.presentation.about.AboutScreen
import com.sportclubai.presentation.student.*

@Composable
fun AppNavigation(isAuthenticated: Boolean) {
    val navController = rememberNavController()

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute != Screen.Login.route && currentRoute != Screen.RegisterClub.route && currentRoute != Screen.Splash.route) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.RegisterClub.route) { RegisterClubScreen(navController) }
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.CoachDashboard.route) { com.sportclubai.presentation.coach.CoachProfileScreen(navController) }
        composable(Screen.Students.route) { StudentsScreen(navController) }
        composable(Screen.AddStudent.route) { com.sportclubai.presentation.students.AddStudentScreen(navController) }
        composable(Screen.StudentProfile.route) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: return@composable
            com.sportclubai.presentation.studentprofile.StudentProfileScreen(navController, studentId)
        }
        composable(Screen.EditStudent.route) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: return@composable
            com.sportclubai.presentation.studentprofile.EditStudentScreen(navController, studentId)
        }
        composable(Screen.Attendance.route) { com.sportclubai.presentation.attendance.AttendanceScreen(navController) }
        composable(Screen.AttendanceSummary.route) { com.sportclubai.presentation.attendance.AttendanceSummaryScreen(navController) }
        composable(Screen.Payments.route) { com.sportclubai.presentation.payments.PaymentsScreen(navController) }
        composable(Screen.CreatePayment.route) { com.sportclubai.presentation.payments.CreatePaymentScreen(navController) }
        
        composable(Screen.Coaches.route) { com.sportclubai.presentation.coaches.CoachesScreen(navController) }
        composable(Screen.AddCoach.route) { com.sportclubai.presentation.coaches.AddCoachScreen(navController) }
        composable(Screen.CoachDetail.route) { backStackEntry ->
            val coachId = backStackEntry.arguments?.getString("coachId") ?: return@composable
            com.sportclubai.presentation.coaches.CoachDetailScreen(navController, coachId)
        }
        composable(Screen.EditCoach.route) { backStackEntry ->
            val coachId = backStackEntry.arguments?.getString("coachId") ?: return@composable
            com.sportclubai.presentation.coaches.EditCoachScreen(navController, coachId)
        }
        
        composable(Screen.Classes.route) { com.sportclubai.presentation.classes.ClassesScreen(navController) }
        composable(Screen.CreateClass.route) { com.sportclubai.presentation.classes.CreateClassScreen(navController) }
        composable(Screen.ClassDetail.route) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId") ?: return@composable
            com.sportclubai.presentation.classes.ClassDetailScreen(navController, classId)
        }
        composable(Screen.EditClass.route) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId") ?: return@composable
            com.sportclubai.presentation.classes.EditClassScreen(navController, classId)
        }
        
        composable(Screen.Messages.route) { com.sportclubai.presentation.messages.MessageListScreen(navController) }
        composable(Screen.Chat.route) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@composable
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            com.sportclubai.presentation.messages.ChatScreen(navController, type, id)
        }
        
        composable(Screen.AITraining.route) { AITrainingScreen(navController) }
        composable(Screen.Calendar.route) { CalendarScreen(navController) }
        composable(Screen.Notifications.route) { NotificationsScreen(navController) }
        composable(Screen.NotificationSettings.route) { com.sportclubai.presentation.notifications.NotificationSettingsScreen(navController) }
        composable(Screen.NotificationDetail.route) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId") ?: return@composable
            com.sportclubai.presentation.notifications.NotificationDetailScreen(navController, notificationId)
        }
        composable(Screen.Reports.route) { ReportsScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.CoachProfile.route) { CoachProfileScreen(navController) }
        composable(Screen.StudentPanel.route) { StudentHomeScreen(navController) }
        composable(Screen.MyProfile.route) { MyProfileScreen(navController) }
        composable(Screen.MyAttendance.route) { MyAttendanceScreen(navController) }
        composable(Screen.MyPayments.route) { MyPaymentsScreen(navController) }
        composable(Screen.MySchedule.route) { MyScheduleScreen(navController) }
        composable(Screen.MyMessages.route) { com.sportclubai.presentation.messages.MessageListScreen(navController) }
        composable(Screen.MyTraining.route) { MyTrainingScreen(navController) }
        composable(Screen.MyBeltProgress.route) { MyBeltProgressScreen(navController) }
        composable(Screen.MySettings.route) { MySettingsScreen(navController) }
        composable(Screen.MyNotifications.route) { com.sportclubai.presentation.notifications.NotificationsScreen(navController) }
        composable(
            route = "training_plan_detail/{planId}",
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            com.sportclubai.presentation.training.TrainingPlanDetailScreen(navController, planId)
        }
        composable("subscription_dashboard") { com.sportclubai.presentation.subscription.SubscriptionDashboardScreen(navController) }
        composable("upgrade_plan") { com.sportclubai.presentation.subscription.UpgradePlanScreen(navController) }
        composable("billing_history") { com.sportclubai.presentation.subscription.BillingHistoryScreen(navController) }
        composable("white_label_settings") { com.sportclubai.presentation.subscription.WhiteLabelSettingsScreen(navController) }
        
        composable(Screen.ParentPanel.route) { ParentPanelScreen(navController) }
        composable(Screen.Competition.route) { CompetitionScreen(navController) }
        composable(Screen.BeltExams.route) { com.sportclubai.presentation.exam.ExamDashboardScreen(navController) }
        composable("create_exam") { com.sportclubai.presentation.exam.CreateExamScreen(navController) }
        composable(
            route = "exam_detail/{examId}",
            arguments = listOf(navArgument("examId") { type = NavType.StringType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getString("examId") ?: ""
            com.sportclubai.presentation.exam.ExamDetailScreen(navController, examId)
        }
        composable(
            route = "exam_evaluation/{examId}/{studentId}",
            arguments = listOf(
                navArgument("examId") { type = NavType.StringType },
                navArgument("studentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getString("examId") ?: ""
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            com.sportclubai.presentation.exam.ExamEvaluationScreen(navController, examId, studentId)
        }
        composable(
            route = "certificate_preview/{examId}/{studentId}",
            arguments = listOf(
                navArgument("examId") { type = NavType.StringType },
                navArgument("studentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getString("examId") ?: ""
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            com.sportclubai.presentation.exam.CertificatePreviewScreen(navController, examId, studentId)
        }
        composable(Screen.Gallery.route) { GalleryScreen(navController) }
        composable(Screen.Documents.route) { DocumentsScreen(navController) }
        composable(Screen.About.route) { AboutScreen(navController) }
        
        composable("advanced_settings") { com.sportclubai.presentation.settings.AdvancedSettingsScreen(navController) }
        composable("backup_dashboard") { com.sportclubai.presentation.backup.BackupDashboardScreen(navController) }
        composable("monitoring_dashboard") { com.sportclubai.presentation.monitoring.MonitoringDashboardScreen(navController) }
        composable("audit_logs") { com.sportclubai.presentation.audit.AuditLogScreen(navController) }
        composable("export_screen") { com.sportclubai.presentation.export.ExportScreen(navController) }
        composable("global_search") { com.sportclubai.presentation.search.GlobalSearchScreen(navController) }
    }
}
