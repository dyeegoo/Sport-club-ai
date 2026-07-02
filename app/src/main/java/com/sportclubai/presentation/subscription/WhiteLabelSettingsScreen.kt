package com.sportclubai.presentation.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.ClubBranding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteLabelSettingsScreen(
    navController: NavController,
    viewModel: WhiteLabelSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var appName by remember { mutableStateOf("") }
    var primaryColor by remember { mutableStateOf("") }
    var customDomain by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is WhiteLabelState.Success) {
            val branding = (uiState as WhiteLabelState.Success).branding
            appName = branding.appName ?: ""
            primaryColor = branding.primaryColorHex ?: ""
            customDomain = branding.customDomain ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("White Label Settings") },
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
                is WhiteLabelState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is WhiteLabelState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadBranding() }) { Text("Retry") }
                    }
                }
                is WhiteLabelState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!state.hasAccess) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Text(
                                    "White Label features require the Enterprise plan. Please upgrade to use these settings.",
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        OutlinedTextField(
                            value = appName,
                            onValueChange = { appName = it },
                            label = { Text("Custom App Name") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.hasAccess
                        )

                        OutlinedTextField(
                            value = primaryColor,
                            onValueChange = { primaryColor = it },
                            label = { Text("Primary Color (Hex)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.hasAccess
                        )

                        OutlinedTextField(
                            value = customDomain,
                            onValueChange = { customDomain = it },
                            label = { Text("Custom Domain") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.hasAccess
                        )

                        Button(
                            onClick = {
                                viewModel.saveBranding(
                                    ClubBranding(
                                        clubId = state.branding.clubId,
                                        appName = appName,
                                        primaryColorHex = primaryColor,
                                        customDomain = customDomain
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.hasAccess
                        ) {
                            Text("Save Settings")
                        }
                    }
                }
            }
        }
    }
}
