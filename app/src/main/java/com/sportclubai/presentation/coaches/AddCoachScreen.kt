package com.sportclubai.presentation.coaches

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
fun AddCoachScreen(
    navController: NavController,
    viewModel: AddCoachViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val fullName by viewModel.fullName.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val email by viewModel.email.collectAsState()
    val specialization by viewModel.specialization.collectAsState()
    val status by viewModel.status.collectAsState()
    val notes by viewModel.notes.collectAsState()

    var statusDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AddCoachState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Coach") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState is AddCoachState.Error) {
                Text(
                    text = (uiState as AddCoachState.Error).message,
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
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { viewModel.phoneNumber.value = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.email.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = specialization,
                onValueChange = { viewModel.specialization.value = it },
                label = { Text("Specialization (e.g. Karate, Boxing)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Status Dropdown
            ExposedDropdownMenuBox(
                expanded = statusDropdownExpanded,
                onExpandedChange = { statusDropdownExpanded = !statusDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = statusDropdownExpanded,
                    onDismissRequest = { statusDropdownExpanded = false }
                ) {
                    listOf("ACTIVE", "INACTIVE").forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = {
                                viewModel.status.value = s
                                statusDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveCoach() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AddCoachState.Loading
            ) {
                if (uiState is AddCoachState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Coach")
                }
            }
        }
    }
}
