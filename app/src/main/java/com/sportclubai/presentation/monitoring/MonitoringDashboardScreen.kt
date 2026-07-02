package com.sportclubai.presentation.monitoring

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringDashboardScreen(
    navController: NavController,
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Monitoring") },
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
                is MonitoringState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is MonitoringState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is MonitoringState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MonitoringCard("Active Users", state.stats.activeUsers.toString())
                        MonitoringCard("Firestore Reads", state.stats.firestoreReads.toString())
                        MonitoringCard("Firestore Writes", state.stats.firestoreWrites.toString())
                        MonitoringCard("Storage Used", "${state.stats.storageBytesUsed / (1024 * 1024)} MB")
                        MonitoringCard("AI Requests", state.stats.aiRequestsCount.toString())
                        MonitoringCard("Error Count", state.stats.errorCount.toString(), isAlert = state.stats.errorCount > 0)
                    }
                }
            }
        }
    }
}

@Composable
fun MonitoringCard(title: String, value: String, isAlert: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAlert) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = if (isAlert) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = if (isAlert) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary)
        }
    }
}
