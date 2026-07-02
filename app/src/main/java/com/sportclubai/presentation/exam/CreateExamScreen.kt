package com.sportclubai.presentation.exam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.ExamCriteria
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExamScreen(
    navController: NavController,
    viewModel: CreateExamViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var sportType by remember { mutableStateOf("") }
    var targetBelt by remember { mutableStateOf("") }

    val createStatus by viewModel.createStatus.collectAsState()

    LaunchedEffect(createStatus) {
        if (createStatus is CreateExamStatus.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Belt Exam") },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Exam Title") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = sportType,
                onValueChange = { sportType = it },
                label = { Text("Sport Type") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = targetBelt,
                onValueChange = { targetBelt = it },
                label = { Text("Target Belt") },
                modifier = Modifier.fillMaxWidth()
            )

            val defaultCriteria = remember {
                listOf(
                    ExamCriteria(name = "Technique", maxScore = 10.0, minPassScore = 5.0),
                    ExamCriteria(name = "Forms (Poomsae)", maxScore = 10.0, minPassScore = 5.0),
                    ExamCriteria(name = "Sparring", maxScore = 10.0, minPassScore = 5.0),
                    ExamCriteria(name = "Fitness", maxScore = 10.0, minPassScore = 5.0),
                    ExamCriteria(name = "Discipline", maxScore = 10.0, minPassScore = 5.0)
                )
            }

            Text("Default Criteria Included:", style = MaterialTheme.typography.titleMedium)
            defaultCriteria.forEach { crit ->
                Text("- ${crit.name} (Max: ${crit.maxScore}, Min: ${crit.minPassScore})", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            if (createStatus is CreateExamStatus.Error) {
                Text(
                    text = (createStatus as CreateExamStatus.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { 
                    viewModel.createExam(title, sportType, targetBelt, Date().time + 86400000, defaultCriteria) 
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = createStatus !is CreateExamStatus.Loading && title.isNotBlank() && targetBelt.isNotBlank()
            ) {
                if (createStatus is CreateExamStatus.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Create Exam")
                }
            }
        }
    }
}
