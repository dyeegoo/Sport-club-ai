package com.sportclubai.presentation.exam

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
import com.sportclubai.domain.model.ExamCriteria
import com.sportclubai.domain.model.ExamResult
import com.sportclubai.domain.model.ExamStudent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamEvaluationScreen(
    navController: NavController,
    examId: String,
    studentId: String,
    viewModel: ExamEvaluationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(examId, studentId) {
        viewModel.loadEvaluationData(examId, studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evaluate Student") },
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
                is ExamEvaluationState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ExamEvaluationState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadEvaluationData(examId, studentId) }) { Text("Retry") }
                    }
                }
                is ExamEvaluationState.Success -> {
                    EvaluationForm(
                        student = state.student,
                        criteriaList = state.criteria,
                        onSave = { results ->
                            viewModel.saveEvaluation(examId, state.student, results)
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EvaluationForm(
    student: ExamStudent,
    criteriaList: List<ExamCriteria>,
    onSave: (List<ExamResult>) -> Unit
) {
    // Keep local state for each criterion
    val resultsState = remember { mutableStateMapOf<String, ExamResult>() }

    // Initialize with existing or empty
    LaunchedEffect(criteriaList, student) {
        criteriaList.forEach { crit ->
            val existing = student.results.find { it.criteriaName == crit.name }
            resultsState[crit.name] = existing ?: ExamResult(criteriaName = crit.name, score = crit.maxScore / 2.0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Student: ${student.studentName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Target Belt: ${student.targetBelt}", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(8.dp))

        criteriaList.forEach { crit ->
            val currentResult = resultsState[crit.name] ?: ExamResult(criteriaName = crit.name)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(crit.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Min Pass: ${crit.minPassScore} | Max: ${crit.maxScore}", style = MaterialTheme.typography.bodySmall)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = currentResult.score.toFloat(),
                            onValueChange = { 
                                resultsState[crit.name] = currentResult.copy(score = it.toDouble()) 
                            },
                            valueRange = 0f..crit.maxScore.toFloat(),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(String.format("%.1f", currentResult.score), fontWeight = FontWeight.Bold)
                    }
                    
                    OutlinedTextField(
                        value = currentResult.notes,
                        onValueChange = { resultsState[crit.name] = currentResult.copy(notes = it) },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSave(resultsState.values.toList()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Evaluation")
        }
    }
}
