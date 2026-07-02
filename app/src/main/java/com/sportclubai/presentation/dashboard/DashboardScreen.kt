package com.sportclubai.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.core.navigation.Screen
import com.sportclubai.domain.model.DashboardData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is DashboardState.Error) {
            val error = (uiState as DashboardState.Error).message
            if (error.contains("Unauthorized") || error.contains("User not authenticated")) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState is DashboardState.Success) (uiState as DashboardState.Success).data.clubName else "Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
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
                is DashboardState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DashboardState.Error -> {
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
                is DashboardState.Success -> {
                    DashboardContent(data = state.data, navController = navController)
                }
            }
        }
    }
}

@Composable
fun DashboardContent(data: DashboardData, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(modifier = Modifier.weight(1f), title = "Students", value = data.totalStudents.toString(), icon = Icons.Default.Person)
            StatCard(modifier = Modifier.weight(1f), title = "Coaches", value = data.totalCoaches.toString(), icon = Icons.Default.AccountBox)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(modifier = Modifier.weight(1f), title = "Active Classes", value = data.activeClassesToday.toString(), icon = Icons.Default.PlayArrow)
            StatCard(modifier = Modifier.weight(1f), title = "Exams", value = data.upcomingBeltExams.toString(), icon = Icons.Default.Star)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Today's Attendance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.height(8.dp))
                Text(data.attendanceSummary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Monthly Revenue", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$${data.monthlyRevenue}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Quick Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Grid
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionCard(modifier = Modifier.weight(1f), title = "Students", icon = Icons.Default.Person, onClick = { navController.navigate(Screen.Students.route) })
                ActionCard(modifier = Modifier.weight(1f), title = "Coaches", icon = Icons.Default.AccountBox, onClick = { navController.navigate(Screen.Coaches.route) })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionCard(modifier = Modifier.weight(1f), title = "Classes", icon = Icons.Default.DateRange, onClick = { navController.navigate(Screen.Classes.route) })
                ActionCard(modifier = Modifier.weight(1f), title = "Attendance", icon = Icons.Default.CheckCircle, onClick = { navController.navigate(Screen.Attendance.route) })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionCard(modifier = Modifier.weight(1f), title = "Payments", icon = Icons.Default.ShoppingCart, onClick = { navController.navigate(Screen.Payments.route) })
                ActionCard(modifier = Modifier.weight(1f), title = "Messages", icon = Icons.Default.Email, onClick = { navController.navigate(Screen.Messages.route) })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionCard(modifier = Modifier.weight(1f), title = "Subscription", icon = Icons.Default.Star, onClick = { navController.navigate("subscription_dashboard") })
                ActionCard(modifier = Modifier.weight(1f), title = "Search", icon = Icons.Default.Search, onClick = { navController.navigate("global_search") })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionCard(modifier = Modifier.weight(1f), title = "Settings", icon = Icons.Default.Settings, onClick = { navController.navigate("advanced_settings") })
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionCard(modifier: Modifier = Modifier, title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

