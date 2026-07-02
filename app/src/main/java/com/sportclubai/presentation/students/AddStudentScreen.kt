package com.sportclubai.presentation.students

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentScreen(
    navController: NavController,
    viewModel: AddStudentViewModel = hiltViewModel()
) {
    val fullName by viewModel.fullName.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val parentName by viewModel.parentName.collectAsState()
    val parentPhone by viewModel.parentPhone.collectAsState()
    val birthDate by viewModel.birthDate.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val height by viewModel.height.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val beltLevel by viewModel.beltLevel.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AddStudentState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Student") },
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
        if (uiState is AddStudentState.LimitReached) {
            AlertDialog(
                onDismissRequest = { navController.popBackStack() },
                title = { Text("Student Limit Reached") },
                text = { Text("You have reached the maximum number of students allowed on your current plan. Please upgrade your subscription to add more students.") },
                confirmButton = {
                    Button(onClick = { 
                        navController.popBackStack()
                        navController.navigate("subscription_dashboard") 
                    }) {
                        Text("Upgrade Plan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState is AddStudentState.Error) {
                Text(
                    text = (uiState as AddStudentState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            OutlinedTextField(
                value = fullName,
                onValueChange = { viewModel.fullName.value = it },
                label = { Text("Full Name *") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { viewModel.phoneNumber.value = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = parentName,
                onValueChange = { viewModel.parentName.value = it },
                label = { Text("Parent Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = parentPhone,
                onValueChange = { viewModel.parentPhone.value = it },
                label = { Text("Parent Phone") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = birthDate,
                onValueChange = { viewModel.birthDate.value = it },
                label = { Text("Birth Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = height,
                onValueChange = { viewModel.height.value = it },
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = weight,
                onValueChange = { viewModel.weight.value = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.addStudent() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AddStudentState.Loading
            ) {
                if (uiState is AddStudentState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Student")
                }
            }
        }
    }
}
