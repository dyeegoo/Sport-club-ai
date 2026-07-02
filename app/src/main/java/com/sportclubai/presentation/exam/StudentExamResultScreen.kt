package com.sportclubai.presentation.exam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sportclubai.domain.model.ExamResult
import com.sportclubai.domain.model.ExamStudent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentExamResultScreen(
    navController: NavController,
    examTitle: String,
    studentResult: ExamStudent
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exam Results") },
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
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(examTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Result:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (studentResult.passed) "PASSED" else "FAILED",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (studentResult.passed) androidx.compose.ui.graphics.Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Score: ${studentResult.totalScore}")
                    Text("Target Belt: ${studentResult.targetBelt}")
                }
            }

            Text("Breakdown", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(studentResult.results) { res ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(res.criteriaName, fontWeight = FontWeight.Bold)
                                Text("Score: ${res.score}")
                            }
                            if (res.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Coach Note: ${res.notes}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            
            if (studentResult.passed) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { navController.navigate("certificate_preview/examId/${studentResult.studentId}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Certificate")
                }
            }
        }
    }
}
