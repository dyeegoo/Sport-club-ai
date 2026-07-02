package com.sportclubai.presentation.export

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    navController: NavController,
    viewModel: ExportViewModel = hiltViewModel()
) {
    var selectedType by remember { mutableStateOf("Students") }
    var selectedFormat by remember { mutableStateOf("CSV") }
    val types = listOf("Students", "Attendance", "Payments", "Classes", "Coaches", "Analytics")
    val formats = listOf("CSV", "Excel", "PDF")
    
    val exportStatus by viewModel.exportStatus.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export Data") },
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
                .verticalScroll(rememberScrollState())
        ) {
            Text("Select Data Type", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            types.forEach { type ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedType == type,
                        onClick = { selectedType = type }
                    )
                    Text(type)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Select Format", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            formats.forEach { format ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == format,
                        onClick = { selectedFormat = format }
                    )
                    Text(format)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            if (exportStatus is ExportState.Error) {
                Text((exportStatus as ExportState.Error).message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            } else if (exportStatus is ExportState.Success) {
                Text("Export ready: ${(exportStatus as ExportState.Success).url}", color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { viewModel.requestExport(selectedType, selectedFormat) },
                modifier = Modifier.fillMaxWidth(),
                enabled = exportStatus !is ExportState.Loading
            ) {
                if (exportStatus is ExportState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Generate Export")
                }
            }
        }
    }
}
