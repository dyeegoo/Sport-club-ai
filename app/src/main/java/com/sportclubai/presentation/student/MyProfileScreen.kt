package com.sportclubai.presentation.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.sportclubai.presentation.studentprofile.DetailRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    navController: NavController,
    viewModel: MyProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is MyProfileState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MyProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProfile() }) {
                            Text("Retry")
                        }
                    }
                }
                is MyProfileState.Success -> {
                    MyProfileContent(student = state.student)
                }
            }
        }
    }
}

@Composable
fun MyProfileContent(student: Student) {
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
        
        if (student.notes.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Medical / Coach Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(student.notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
