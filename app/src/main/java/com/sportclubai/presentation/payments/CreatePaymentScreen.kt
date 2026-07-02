package com.sportclubai.presentation.payments

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
fun CreatePaymentScreen(
    navController: NavController,
    viewModel: CreatePaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val students by viewModel.students.collectAsState()
    
    val selectedStudentId by viewModel.selectedStudentId.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val paymentType by viewModel.paymentType.collectAsState()
    val status by viewModel.status.collectAsState()
    val dueDate by viewModel.dueDate.collectAsState()
    val notes by viewModel.notes.collectAsState()

    var studentDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is CreatePaymentState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Payment") },
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
            if (uiState is CreatePaymentState.Error) {
                Text(
                    text = (uiState as CreatePaymentState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Student Dropdown
            ExposedDropdownMenuBox(
                expanded = studentDropdownExpanded,
                onExpandedChange = { studentDropdownExpanded = !studentDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedStudentName = students.find { it.studentId == selectedStudentId }?.fullName ?: "Select Student"
                OutlinedTextField(
                    value = selectedStudentName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Student *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = studentDropdownExpanded,
                    onDismissRequest = { studentDropdownExpanded = false }
                ) {
                    students.forEach { student ->
                        DropdownMenuItem(
                            text = { Text(student.fullName) },
                            onClick = {
                                viewModel.selectedStudentId.value = student.studentId
                                studentDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel.amount.value = it },
                label = { Text("Amount *") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Payment Type Dropdown
            ExposedDropdownMenuBox(
                expanded = typeDropdownExpanded,
                onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = paymentType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeDropdownExpanded,
                    onDismissRequest = { typeDropdownExpanded = false }
                ) {
                    listOf("MONTHLY_FEE", "REGISTRATION_FEE", "PENALTY", "OTHER").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.paymentType.value = type
                                typeDropdownExpanded = false
                            }
                        )
                    }
                }
            }
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
                    listOf("PAID", "PENDING", "OVERDUE").forEach { s ->
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
                value = dueDate,
                onValueChange = { viewModel.dueDate.value = it },
                label = { Text("Due Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.createPayment() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is CreatePaymentState.Loading
            ) {
                if (uiState is CreatePaymentState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Create Payment")
                }
            }
        }
    }
}
