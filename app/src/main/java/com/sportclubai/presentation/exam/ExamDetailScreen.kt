package com.sportclubai.presentation.exam

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.Exam
import com.sportclubai.domain.model.ExamStudent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailScreen(
    navController: NavController,
    examId: String,
    viewModel: ExamDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(examId) {
        viewModel.loadExam(examId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState is ExamDetailState.Success) (uiState as ExamDetailState.Success).exam.title else "Exam Details") },
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
                is ExamDetailState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ExamDetailState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadExam(examId) }) { Text("Retry") }
                    }
                }
                is ExamDetailState.Success -> {
                    val exam = state.exam
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Exam Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Target Belt: ${exam.targetBelt}")
                                Text("Status: ${exam.status}")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Enrolled Students", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (exam.students.isEmpty()) {
                            Text("No students enrolled yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(exam.students) { student ->
                                    StudentExamCard(student = student, onClick = {
                                        navController.navigate("exam_evaluation/${exam.id}/${student.studentId}")
                                    })
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
fun StudentExamCard(student: ExamStudent, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(student.studentName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Current Belt: ${student.currentBelt}", style = MaterialTheme.typography.bodySmall)
            }
            if (student.results.isNotEmpty()) {
                val iconColor = if (student.passed) Color(0xFF4CAF50) else Color(0xFFF44336)
                Icon(Icons.Default.CheckCircle, contentDescription = "Evaluated", tint = iconColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (student.passed) "PASS" else "FAIL", fontWeight = FontWeight.Bold, color = iconColor)
            } else {
                Text("Pending", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
