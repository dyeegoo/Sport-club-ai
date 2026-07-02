package com.sportclubai.presentation.subscription

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
import com.sportclubai.domain.model.Invoice
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingHistoryScreen(
    navController: NavController,
    viewModel: BillingHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Billing History") },
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
                is BillingHistoryState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is BillingHistoryState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadInvoices() }) { Text("Retry") }
                    }
                }
                is BillingHistoryState.Success -> {
                    if (state.invoices.isEmpty()) {
                        Text("No billing history found.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.invoices) { invoice ->
                                InvoiceCard(invoice)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceCard(invoice: Invoice) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(invoice.date)), fontWeight = FontWeight.Bold)
                Text("${invoice.amount} ${invoice.currency}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Status: ${invoice.status}", style = MaterialTheme.typography.bodySmall)
            if (invoice.invoiceUrl != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Download PDF", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
