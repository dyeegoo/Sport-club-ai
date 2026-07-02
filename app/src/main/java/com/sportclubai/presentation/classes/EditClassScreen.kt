package com.sportclubai.presentation.classes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClassScreen(
    navController: NavController,
    classId: String,
    viewModel: EditClassViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coaches by viewModel.coaches.collectAsState()
    
    val className by viewModel.className.collectAsState()
    val selectedCoachId by viewModel.selectedCoachId.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val maxStudents by viewModel.maxStudents.collectAsState()
    val location by viewModel.location.collectAsState()
    val selectedDays by viewModel.selectedDays.collectAsState()
    val status by viewModel.status.collectAsState()

    var coachDropdownExpanded by remember { mutableStateOf(false) }
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(classId) {
        viewModel.loadClass(classId)
    }

    LaunchedEffect(uiState) {
        if (uiState is EditClassState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Class") },
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
        if (uiState is EditClassState.Loading && className.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
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
                if (uiState is EditClassState.Error) {
                    Text(
                        text = (uiState as EditClassState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                OutlinedTextField(
                    value = className,
                    onValueChange = { viewModel.className.value = it },
                    label = { Text("Class Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Coach Dropdown
                ExposedDropdownMenuBox(
                    expanded = coachDropdownExpanded,
                    onExpandedChange = { coachDropdownExpanded = !coachDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectedCoach = coaches.find { it.coachId == selectedCoachId }
                    OutlinedTextField(
                        value = selectedCoach?.fullName ?: "Select Coach",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Coach") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = coachDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = coachDropdownExpanded,
                        onDismissRequest = { coachDropdownExpanded = false }
                    ) {
                        coaches.forEach { coach ->
                            DropdownMenuItem(
                                text = { Text(coach.fullName) },
                                onClick = {
                                    viewModel.selectedCoachId.value = coach.coachId
                                    coachDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Schedule Days", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WEEKDAYS.forEach { day ->
                        FilterChip(
                            selected = selectedDays.contains(day),
                            onClick = { viewModel.toggleDay(day) },
                            label = { Text(day) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { viewModel.startTime.value = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { viewModel.endTime.value = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = maxStudents,
                    onValueChange = { viewModel.maxStudents.value = it },
                    label = { Text("Max Students *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { viewModel.location.value = it },
                    label = { Text("Location (Optional)") },
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
                        listOf("ACTIVE", "CANCELLED").forEach { s ->
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
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.updateClass() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is EditClassState.Loading
                ) {
                    if (uiState is EditClassState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}
