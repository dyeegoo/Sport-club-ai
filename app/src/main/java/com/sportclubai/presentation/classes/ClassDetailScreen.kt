package com.sportclubai.presentation.classes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailScreen(
    navController: NavController,
    classId: String,
    viewModel: ClassDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(classId) {
        viewModel.loadClass(classId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Class Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is ClassDetailState.Success) {
                        IconButton(onClick = { navController.navigate("edit_class/$classId") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
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
                is ClassDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ClassDetailState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadClass(classId) }) {
                            Text("Retry")
                        }
                    }
                }
                is ClassDetailState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        ClassInfoHeader(
                            sportClass = state.sportClass,
                            coachName = state.coach?.fullName ?: "No Coach Assigned"
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Enrolled Students", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (state.students.isEmpty()) {
                            Text("No students enrolled yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(state.students) { student ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape),
                                                    color = MaterialTheme.colorScheme.primaryContainer
                                                ) {
                                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp))
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(student.fullName, style = MaterialTheme.typography.bodyLarge)
                                            }
                                            IconButton(onClick = { viewModel.removeStudent(classId, student.studentId) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Remove Student", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClassInfoHeader(sportClass: com.sportclubai.domain.model.SportClass, coachName: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(sportClass.className, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            val statusColor = if (sportClass.status == "ACTIVE") Color(0xFF4CAF50) else Color(0xFFF44336)
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = sportClass.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Coach:", fontWeight = FontWeight.Bold)
                Text(coachName)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Schedule:", fontWeight = FontWeight.Bold)
                Text(sportClass.scheduleDays.joinToString(", ") + " (${sportClass.startTime} - ${sportClass.endTime})")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Location:", fontWeight = FontWeight.Bold)
                Text(if (sportClass.location.isNotEmpty()) sportClass.location else "N/A")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Capacity:", fontWeight = FontWeight.Bold)
                Text("${sportClass.assignedStudents.size} / ${sportClass.maxStudents}")
            }
        }
    }
}
