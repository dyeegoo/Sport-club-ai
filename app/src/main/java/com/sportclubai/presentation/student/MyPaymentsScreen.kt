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
import com.sportclubai.domain.model.Payment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPaymentsScreen(
    navController: NavController,
    viewModel: MyPaymentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Payments") },
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
                is MyPaymentsState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is MyPaymentsState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadData() }) { Text("Retry") }
                    }
                }
                is MyPaymentsState.Success -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Payment History", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        if (state.payments.isEmpty()) {
                            Text("No payment records found.")
                        } else {
                            LazyColumn {
                                items(state.payments) { payment ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Row(
                                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(payment.paymentType, fontWeight = FontWeight.Medium)
                                                Text("Due: ${payment.dueDate}", style = MaterialTheme.typography.bodySmall)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("${payment.amount} ${payment.currency}", fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = payment.status,
                                                    color = when (payment.status) {
                                                        "PAID" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                                        "PENDING" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                                        "OVERDUE" -> androidx.compose.ui.graphics.Color(0xFFF44336)
                                                        else -> MaterialTheme.colorScheme.primary
                                                    },
                                                    style = MaterialTheme.typography.labelMedium
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
}
