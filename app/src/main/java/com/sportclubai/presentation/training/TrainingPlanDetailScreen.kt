package com.sportclubai.presentation.training

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.TrainingPlan
import com.sportclubai.domain.model.TrainingWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlanDetailScreen(
    navController: NavController,
    planId: String,
    viewModel: TrainingPlanDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(planId) {
        viewModel.loadPlan(planId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState is TrainingPlanDetailState.Success) (uiState as TrainingPlanDetailState.Success).plan.title else "Plan Details") },
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
                is TrainingPlanDetailState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is TrainingPlanDetailState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadPlan(planId) }) { Text("Retry") }
                    }
                }
                is TrainingPlanDetailState.Success -> {
                    val plan = state.plan
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Plan Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Difficulty: ${plan.difficulty}")
                                    if (plan.coachNotes.isNotBlank()) {
                                        Text("Coach Notes: ${plan.coachNotes}")
                                    }
                                }
                            }
                        }

                        items(plan.weeks) { week ->
                            WeekCard(week = week, planId = plan.id, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeekCard(week: TrainingWeek, planId: String, viewModel: TrainingPlanDetailViewModel) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Week ${week.weekNumber}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (week.focus.isNotBlank()) {
                Text("Focus: ${week.focus}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            week.days.forEach { day ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(day.dayOfWeek, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    day.exercises.forEach { exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.toggleExercise(planId, week.weekNumber, day.dayOfWeek, exercise.id, !exercise.isCompleted)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(exercise.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text("${exercise.durationMinutes} min - ${exercise.category}", style = MaterialTheme.typography.bodySmall)
                            }
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = if (exercise.isCompleted) Color(0xFF4CAF50) else Color.LightGray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Divider()
                    }
                }
            }
        }
    }
}
