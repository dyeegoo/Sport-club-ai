package com.sportclubai.presentation.backup

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
import com.sportclubai.domain.model.BackupMetadata
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupDashboardScreen(
    navController: NavController,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
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
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Button(
                onClick = { viewModel.triggerBackup() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Manual Backup")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Backup History", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            when (val state = uiState) {
                is BackupState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is BackupState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is BackupState.Success -> {
                    if (state.backups.isEmpty()) {
                        Text("No backups found.", modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.backups) { backup ->
                                BackupCard(backup) { viewModel.restoreBackup(backup.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupCard(backup: BackupMetadata, onRestore: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(backup.timestamp)), fontWeight = FontWeight.Bold)
                Text(backup.status.uppercase(), color = if (backup.status == "success") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Type: ${backup.type.uppercase()}", style = MaterialTheme.typography.bodySmall)
            Text("Size: ${backup.sizeBytes / 1024} KB", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // اصلاح اینجا انجام شد:
            OutlinedButton(
                onClick = onRestore,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restore Backup")
            }
        }
    }
}
