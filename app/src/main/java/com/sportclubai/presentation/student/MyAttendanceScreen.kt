package com.sportclubai.presentation.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sportclubai.domain.model.Attendance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAttendanceScreen(
    navController: NavController,
    viewModel: MyAttendanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Attendance") },
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
                is MyAttendanceState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is MyAttendanceState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadData() }) { Text("Retry") }
                    }
                }
                is MyAttendanceState.Success -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Attendance History", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        if (state.attendances.isEmpty()) {
                            Text("No attendance records found.")
                        } else {
                            LazyColumn {
                                items(state.attendances) { attendance ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Row(
                                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(attendance.date, fontWeight = FontWeight.Medium)
                                            Text(
                                                text = attendance.status,
                                                color = when (attendance.status) {
                                                    "PRESENT" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                                    "ABSENT" -> androidx.compose.ui.graphics.Color(0xFFF44336)
                                                    "LATE" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                                    else -> MaterialTheme.colorScheme.primary
                                                },
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
