package com.sportclubai.presentation.training

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.core.navigation.Screen
import com.sportclubai.domain.model.TrainingPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITrainingDashboardScreen(
    navController: NavController,
    studentId: String?,
    viewModel: AITrainingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // In a real app we'd pass studentId or coach context to load specific plans
    LaunchedEffect(studentId) {
        if (studentId != null) {
            viewModel.loadPlansForStudent(studentId)
        }
    }

    var showGenerateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Training Plans") },
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showGenerateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Generate New Plan")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is AITrainingState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AITrainingState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { if (studentId != null) viewModel.loadPlansForStudent(studentId) }) {
                            Text("Retry")
                        }
                    }
                }
                is AITrainingState.Success -> {
                    if (state.plans.isEmpty()) {
                        Text(
                            "No training plans found. Tap + to generate one.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.plans) { plan ->
                                TrainingPlanCard(plan = plan) {
                                    // Navigate to detailed plan screen
                                    navController.navigate("training_plan_detail/${plan.id}")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showGenerateDialog) {
            GenerateTrainingDialog(
                onDismiss = { showGenerateDialog = false },
                onGenerate = { sport, belt, age, attendance, weak, strong, availability, notes ->
                    showGenerateDialog = false
                    viewModel.generatePlan(
                        studentId = studentId ?: "",
                        sportType = sport,
                        currentBelt = belt,
                        age = age,
                        attendancePercentage = attendance,
                        weakSkills = weak,
                        strongSkills = strong,
                        weeklyAvailabilityDays = availability,
                        targetExamDate = null,
                        coachNotes = notes
                    )
                }
            )
        }
    }
}

@Composable
fun TrainingPlanCard(plan: TrainingPlan, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(plan.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sport: ${plan.sportType}", style = MaterialTheme.typography.bodyMedium)
                Text("Level: ${plan.difficulty.name}", style = MaterialTheme.typography.bodyMedium)
            }
            Text("Duration: ${plan.weeks.size} Weeks", style = MaterialTheme.typography.bodySmall)
            if (plan.coachNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Notes: ${plan.coachNotes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
