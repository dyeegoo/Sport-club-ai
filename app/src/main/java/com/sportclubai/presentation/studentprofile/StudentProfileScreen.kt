package com.sportclubai.presentation.studentprofile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    navController: NavController,
    studentId: String,
    viewModel: StudentProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadStudent(studentId)
    }

    LaunchedEffect(uiState) {
        if (uiState is StudentProfileState.Deleted) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is StudentProfileState.Success) {
                        IconButton(onClick = { navController.navigate("edit_student/$studentId") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { viewModel.deleteStudent() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
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
                is StudentProfileState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is StudentProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadStudent(studentId) }) {
                            Text("Retry")
                        }
                    }
                }
                is StudentProfileState.Success -> {
                    StudentProfileContent(
                        student = state.student, 
                        attendances = state.attendances,
                        payments = state.payments
                    )
                }
                is StudentProfileState.Deleted -> { }
            }
        }
    }
}

@Composable
fun StudentProfileContent(
    student: Student, 
    attendances: List<com.sportclubai.domain.model.Attendance>,
    payments: List<com.sportclubai.domain.model.Payment>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(24.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(student.fullName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(student.membershipStatus, style = MaterialTheme.typography.labelLarge, color = if (student.membershipStatus == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        val totalClasses = attendances.size
        val presentCount = attendances.count { it.status == "PRESENT" || it.status == "LATE" }
        val attendancePercentage = if (totalClasses > 0) (presentCount.toFloat() / totalClasses * 100).toInt() else 0

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Quick Stats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Belt Level:", fontWeight = FontWeight.Medium)
                    Text(student.beltLevel)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Present:", fontWeight = FontWeight.Medium)
                    Text(presentCount.toString())
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Attendance Rate:", fontWeight = FontWeight.Medium)
                    Text("$attendancePercentage%")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Personal Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow("Phone", student.phoneNumber)
                DetailRow("Parent Name", student.parentName)
                DetailRow("Parent Phone", student.parentPhone)
                DetailRow("Birth Date", student.birthDate)
                DetailRow("Gender", student.gender)
                DetailRow("Height", "${student.height} cm")
                DetailRow("Weight", "${student.weight} kg")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (attendances.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recent Attendance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    attendances.take(5).forEach { attendance ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(attendance.date, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = attendance.status,
                                fontWeight = FontWeight.Medium,
                                color = when (attendance.status) {
                                    "PRESENT" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                    "ABSENT" -> androidx.compose.ui.graphics.Color(0xFFF44336)
                                    "LATE" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                    else -> androidx.compose.ui.graphics.Color(0xFF2196F3)
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (payments.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Payment History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    payments.take(5).forEach { payment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(payment.paymentType, style = MaterialTheme.typography.bodyMedium)
                                Text("Due: ${payment.dueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("$${payment.amount}", fontWeight = FontWeight.Bold)
                                Text(
                                    text = payment.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (payment.status) {
                                        "PAID" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        "PENDING" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                        "OVERDUE" -> androidx.compose.ui.graphics.Color(0xFFF44336)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (student.notes.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Coach Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(student.notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Medium)
        }
    }
}

