package com.sportclubai.presentation.exam

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatePreviewScreen(
    navController: NavController,
    examId: String,
    studentId: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Belt Certificate") },
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
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(modifier = Modifier.padding(16.dp).fillMaxWidth().height(300.dp)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Certificate of Achievement", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("This certifies that the student has successfully passed the belt exam.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Digital Signature Placeholder", style = MaterialTheme.typography.labelLarge)
                    Text("QR Code Placeholder", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
