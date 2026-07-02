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
import com.sportclubai.domain.model.SportClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScheduleScreen(
    navController: NavController,
    viewModel: MyScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Schedule") },
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
                is MyScheduleState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is MyScheduleState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadData() }) { Text("Retry") }
                    }
                }
                is MyScheduleState.Success -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("My Classes", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        if (state.classes.isEmpty()) {
                            Text("No upcoming classes found.")
                        } else {
                            LazyColumn {
                                items(state.classes) { sportClass ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                            Text(sportClass.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Days: ${sportClass.scheduleDays.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Time: ${sportClass.startTime} - ${sportClass.endTime}", style = MaterialTheme.typography.bodyMedium)
                                            if (sportClass.notes.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(sportClass.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
}
