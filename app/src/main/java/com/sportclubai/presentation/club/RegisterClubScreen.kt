package com.sportclubai.presentation.club

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.core.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterClubScreen(
    navController: NavController,
    viewModel: RegisterClubViewModel = hiltViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val isRegistering by viewModel.isRegistering.collectAsState()
    val registrationSuccess by viewModel.registrationSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.RegisterClub.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Your Club") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar {
                if (currentStep > 1) {
                    TextButton(onClick = { viewModel.previousStep() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        Spacer(Modifier.width(4.dp))
                        Text("Back")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (currentStep < 3) {
                    Button(onClick = { viewModel.nextStep() }) {
                        Text("Next")
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                } else {
                    Button(
                        onClick = { viewModel.registerClub() },
                        enabled = !isRegistering
                    ) {
                        if (isRegistering) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Complete Registration")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.Check, contentDescription = "Done")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepIndicator(currentStep = currentStep, totalSteps = 3)
            Spacer(modifier = Modifier.height(24.dp))
            
            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            when (currentStep) {
                1 -> OwnerInformationStep(viewModel)
                2 -> ClubInformationStep(viewModel)
                3 -> ApplicationSettingsStep(viewModel)
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..totalSteps) {
            val color = if (i <= currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            Surface(
                shape = MaterialTheme.shapes.small,
                color = color,
                modifier = Modifier
                    .height(8.dp)
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {}
        }
    }
}

@Composable
fun OwnerInformationStep(viewModel: RegisterClubViewModel) {
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val mobileNumber by viewModel.mobileNumber.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    Text("Step 1: Owner Information", style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = firstName,
        onValueChange = { viewModel.firstName.value = it },
        label = { Text("First Name") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = lastName,
        onValueChange = { viewModel.lastName.value = it },
        label = { Text("Last Name") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = email,
        onValueChange = { viewModel.email.value = it },
        label = { Text("Email") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = mobileNumber,
        onValueChange = { viewModel.mobileNumber.value = it },
        label = { Text("Mobile Number") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = password,
        onValueChange = { viewModel.password.value = it },
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { viewModel.confirmPassword.value = it },
        label = { Text("Confirm Password") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ClubInformationStep(viewModel: RegisterClubViewModel) {
    val clubName by viewModel.clubName.collectAsState()
    val sportType by viewModel.sportType.collectAsState()
    val country by viewModel.country.collectAsState()
    val city by viewModel.city.collectAsState()
    val address by viewModel.address.collectAsState()
    val clubPhone by viewModel.clubPhone.collectAsState()
    val website by viewModel.website.collectAsState()

    Text("Step 2: Club Information", style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = clubName,
        onValueChange = { viewModel.clubName.value = it },
        label = { Text("Club Name") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = sportType,
        onValueChange = { viewModel.sportType.value = it },
        label = { Text("Sport Type (e.g. Taekwondo)") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = country,
        onValueChange = { viewModel.country.value = it },
        label = { Text("Country") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = city,
        onValueChange = { viewModel.city.value = it },
        label = { Text("City") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = address,
        onValueChange = { viewModel.address.value = it },
        label = { Text("Address") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = clubPhone,
        onValueChange = { viewModel.clubPhone.value = it },
        label = { Text("Club Phone") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = website,
        onValueChange = { viewModel.website.value = it },
        label = { Text("Website (Optional)") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ApplicationSettingsStep(viewModel: RegisterClubViewModel) {
    val language by viewModel.language.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val timeZone by viewModel.timeZone.collectAsState()

    Text("Step 3: Application Settings", style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = language,
        onValueChange = { viewModel.language.value = it },
        label = { Text("Language") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = currency,
        onValueChange = { viewModel.currency.value = it },
        label = { Text("Currency (e.g. USD, EUR)") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = timeZone,
        onValueChange = { viewModel.timeZone.value = it },
        label = { Text("Time Zone (e.g. UTC, PST)") },
        modifier = Modifier.fillMaxWidth()
    )
}
