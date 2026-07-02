package com.sportclubai.presentation.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.Subscription
import com.sportclubai.domain.model.UsageStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDashboardScreen(
    navController: NavController,
    viewModel: SubscriptionDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription & Billing") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is SubscriptionDashboardState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is SubscriptionDashboardState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadData() }) { Text("Retry") }
                    }
                }
                is SubscriptionDashboardState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SubscriptionCard(
                            subscription = state.subscription,
                            onUpgradeClick = { navController.navigate("upgrade_plan") }
                        )
                        
                        UsageCard(
                            subscription = state.subscription,
                            usage = state.usage
                        )

                        OutlinedButton(
                            onClick = { navController.navigate("billing_history") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View Billing History")
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("white_label_settings") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("White Label Settings")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionCard(subscription: Subscription, onUpgradeClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Current Plan", style = MaterialTheme.typography.titleMedium)
            Text(subscription.plan.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(8.dp))
            if (subscription.isTrial) {
                Text("Status: Active (Trial)", style = MaterialTheme.typography.bodyMedium)
                Text("Trial expires soon", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            } else {
                Text(if (subscription.isActive) "Status: Active" else "Status: Inactive", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onUpgradeClick, modifier = Modifier.fillMaxWidth()) {
                Text("Upgrade Plan")
            }
        }
    }
}

@Composable
fun UsageCard(subscription: Subscription, usage: UsageStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Current Usage", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            UsageProgressRow("Students", usage.studentCount, subscription.limits.maxStudents)
            Spacer(modifier = Modifier.height(8.dp))
            UsageProgressRow("Coaches", usage.coachCount, subscription.limits.maxCoaches)
            Spacer(modifier = Modifier.height(8.dp))
            UsageProgressRow("AI Requests (Monthly)", usage.currentMonthlyAiRequests, subscription.limits.maxMonthlyAiRequests)
        }
    }
}

@Composable
fun UsageProgressRow(label: String, current: Int, max: Int) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("$current / $max", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        val progress = if (max > 0) (current.toFloat() / max.toFloat()).coerceIn(0f, 1f) else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = if (progress >= 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
}
