package com.sportclubai.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.core.navigation.Screen
import com.sportclubai.domain.model.StudentDashboardData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentPanelScreen(
    navController: NavController,
    viewModel: StudentPanelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is StudentPanelState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is StudentPanelState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadData() }) {
                            Text("Retry")
                        }
                    }
                }
                is StudentPanelState.Success -> {
                    StudentDashboardContent(
                        data = state.data,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun StudentDashboardContent(data: StudentDashboardData, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Screen.StudentProfile.route.replace("{studentId}", data.student.studentId)) },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = data.student.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "Belt: ${data.student.beltLevel}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Quick Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardStatCard(
                title = "Attendance",
                value = "${data.attendancePercentage.toInt()}%",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.Attendance.route) } // Assuming student has their own view or reuse
            )
            DashboardStatCard(
                title = "Messages",
                value = "${data.unreadMessagesCount} unread",
                icon = Icons.Default.Email,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.Messages.route) }
            )
        }

        // Training Plan
        Card(
            modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.AITraining.route) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Training")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Weekly Training Plan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = data.weeklyTrainingPlan, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Next Class
        Card(
            modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Calendar.route) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Next Class", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (data.nextClass != null) {
                    Text(text = "${data.nextClass.name} - ${data.nextClass.scheduleDays.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(text = "No upcoming classes", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        
        // Latest Payment
        Card(
            modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Payments.route) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Latest Payment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (data.latestPayment != null) {
                    Text(text = "${data.latestPayment.amount} ${data.latestPayment.currency} - ${data.latestPayment.status}", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(text = "No payment records", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Belt Exam
        Card(
            modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.BeltExams.route) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Belt Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Current: ${data.student.beltLevel}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodySmall)
        }
    }
}
