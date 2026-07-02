package com.sportclubai.presentation.attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    navController: NavController,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Attendance") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mark All Present") },
                            onClick = {
                                viewModel.markAll("PRESENT")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mark All Absent") },
                            onClick = {
                                viewModel.markAll("ABSENT")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear All") },
                            onClick = {
                                // For clear, we might want to delete the records or set status to empty.
                                // In this simple case, we might not have a clear all unless we implement delete.
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("View Summary") },
                            onClick = {
                                navController.navigate("attendance_summary")
                                showMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Date Selector (Placeholder for simple text input or date picker)
            // For now, we'll just display the current date and allow simple edits if needed.
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { viewModel.selectDate(it) },
                label = { Text("Date (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            when (val state = uiState) {
                is AttendanceState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AttendanceState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadData() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is AttendanceState.Success -> {
                    if (state.items.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No students found.")
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.items) { item ->
                                AttendanceItemCard(
                                    item = item,
                                    onStatusSelected = { status ->
                                        viewModel.markAttendance(item.student.studentId, status)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceItemCard(
    item: AttendanceItem,
    onStatusSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(item.student.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusButton("PRESENT", item.attendance?.status, Color(0xFF4CAF50), onStatusSelected)
                StatusButton("ABSENT", item.attendance?.status, Color(0xFFF44336), onStatusSelected)
                StatusButton("LATE", item.attendance?.status, Color(0xFFFF9800), onStatusSelected)
                StatusButton("EXCUSED", item.attendance?.status, Color(0xFF2196F3), onStatusSelected)
            }
        }
    }
}

@Composable
fun StatusButton(
    status: String,
    currentStatus: String?,
    color: Color,
    onClick: (String) -> Unit
) {
    val isSelected = status == currentStatus
    
    Button(
        onClick = { onClick(status) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.padding(2.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status.take(3), style = MaterialTheme.typography.labelMedium) // Short label like "PRE", "ABS"
    }
}

