package com.sportclubai.presentation.studentprofile

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
fun EditStudentScreen(
    navController: NavController,
    studentId: String,
    viewModel: EditStudentViewModel = hiltViewModel()
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
    val membershipStatus by viewModel.membershipStatus.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadStudent(studentId)
    }

    LaunchedEffect(uiState) {
        if (uiState is EditStudentState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Student") },
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
        if (uiState is EditStudentState.Loading && fullName.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState is EditStudentState.Error) {
                    Text(
                        text = (uiState as EditStudentState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                OutlinedTextField(value = fullName, onValueChange = { viewModel.fullName.value = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phoneNumber, onValueChange = { viewModel.phoneNumber.value = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = parentName, onValueChange = { viewModel.parentName.value = it }, label = { Text("Parent Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = parentPhone, onValueChange = { viewModel.parentPhone.value = it }, label = { Text("Parent Phone") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = birthDate, onValueChange = { viewModel.birthDate.value = it }, label = { Text("Birth Date") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = beltLevel, onValueChange = { viewModel.beltLevel.value = it }, label = { Text("Belt Level") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = membershipStatus, onValueChange = { viewModel.membershipStatus.value = it }, label = { Text("Status (Active/Inactive)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = notes, onValueChange = { viewModel.notes.value = it }, label = { Text("Coach Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.updateStudent() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is EditStudentState.Loading
                ) {
                    if (uiState is EditStudentState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Update Student")
                    }
                }
            }
        }
    }
}
