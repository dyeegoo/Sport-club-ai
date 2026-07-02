package com.sportclubai.presentation.subscription

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.SubscriptionPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradePlanScreen(
    navController: NavController,
    viewModel: UpgradePlanViewModel = hiltViewModel()
) {
    val plans = SubscriptionPlan.values().toList()
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.STARTER) }

    val upgradeStatus by viewModel.upgradeStatus.collectAsState()

    LaunchedEffect(upgradeStatus) {
        if (upgradeStatus is UpgradeStatus.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose a Plan") },
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
            Text("Select the plan that fits your club's needs.", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(plans) { plan ->
                    PlanCard(
                        plan = plan,
                        isSelected = selectedPlan == plan,
                        onClick = { selectedPlan = plan }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (upgradeStatus is UpgradeStatus.Error) {
                Text(
                    text = (upgradeStatus as UpgradeStatus.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = { viewModel.upgradePlan(selectedPlan) },
                modifier = Modifier.fillMaxWidth(),
                enabled = upgradeStatus !is UpgradeStatus.Loading
            ) {
                if (upgradeStatus is UpgradeStatus.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Proceed to Checkout")
                }
            }
        }
    }
}

@Composable
fun PlanCard(plan: SubscriptionPlan, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(plan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(getPlanDescription(plan), style = MaterialTheme.typography.bodyMedium)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

fun getPlanDescription(plan: SubscriptionPlan): String {
    return when(plan) {
        SubscriptionPlan.FREE -> "Basic features, limited users."
        SubscriptionPlan.STARTER -> "Good for small clubs. Up to 50 students."
        SubscriptionPlan.PROFESSIONAL -> "Advanced features, AI Training. Up to 200 students."
        SubscriptionPlan.BUSINESS -> "Multiple locations, priority support."
        SubscriptionPlan.ENTERPRISE -> "Unlimited everything, White Label app."
    }
}
