package com.sportclubai.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Settings") },
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
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCategory("General")
            SettingsItem("Theme", "System Default") {}
            SettingsItem("Language", "English") {}
            SettingsItem("Currency", "USD") {}
            SettingsItem("Timezone", "UTC") {}
            
            SettingsCategory("Data & Privacy")
            SettingsItem("Backup & Restore", "Manage backups") { navController.navigate("backup_dashboard") }
            SettingsItem("Export Data", "Download reports") { navController.navigate("export_screen") }
            SettingsItem("Security", "App Check, Environment") { /* Navigate to Security */ }
            SettingsItem("Privacy", "Data retention policies") {}
            
            SettingsCategory("System")
            SettingsItem("Monitoring", "System health & usage") { navController.navigate("monitoring_dashboard") }
            SettingsItem("Audit Logs", "View recent activity") { navController.navigate("audit_logs") }
            SettingsItem("Notification Preferences", "Manage alerts") {}
            SettingsItem("AI Settings", "Customize generation") {}
            
            SettingsCategory("About")
            SettingsItem("App Version", "1.0.0") {}
            SettingsItem("Terms of Service", "") {}
            SettingsItem("Privacy Policy", "") {}
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
