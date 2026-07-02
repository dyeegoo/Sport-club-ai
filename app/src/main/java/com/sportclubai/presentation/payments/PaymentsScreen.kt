package com.sportclubai.presentation.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    navController: NavController,
    viewModel: PaymentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payments") },
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_payment") }) {
                Icon(Icons.Default.Add, contentDescription = "Create Payment")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is PaymentsState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is PaymentsState.Error -> {
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
                is PaymentsState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            DashboardCards(state.totalRevenue, state.outstandingAmount)
                        }

                        if (state.overduePayments.isNotEmpty()) {
                            item {
                                Text("Overdue Payments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            }
                            items(state.overduePayments) { item ->
                                PaymentItemCard(item = item, onMarkPaid = { viewModel.markAsPaid(it) })
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("All Payments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        items(state.payments) { item ->
                            PaymentItemCard(item = item, onMarkPaid = { viewModel.markAsPaid(it) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCards(revenue: Double, outstanding: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Monthly Revenue", style = MaterialTheme.typography.labelMedium, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(4.dp))
                Text("$$revenue", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            }
        }
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Outstanding", style = MaterialTheme.typography.labelMedium, color = Color(0xFFE65100))
                Spacer(modifier = Modifier.height(4.dp))
                Text("$$outstanding", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFBF360C))
            }
        }
    }
}

@Composable
fun PaymentItemCard(item: PaymentItem, onMarkPaid: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.student?.fullName ?: "Unknown Student", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = item.payment.status,
                    fontWeight = FontWeight.Bold,
                    color = when (item.payment.status) {
                        "PAID" -> Color(0xFF4CAF50)
                        "PENDING" -> Color(0xFFFF9800)
                        "OVERDUE" -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Type: ${item.payment.paymentType}", style = MaterialTheme.typography.bodyMedium)
                Text("Amount: $${item.payment.amount}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Due: ${item.payment.dueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            if (item.payment.status != "PAID") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onMarkPaid(item.payment.paymentId) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Mark as Paid")
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Paid on: ${item.payment.paidDate}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
            }
        }
    }
}
