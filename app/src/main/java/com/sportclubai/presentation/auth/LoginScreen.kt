package com.sportclubai.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.core.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val forgotPasswordMessage by viewModel.forgotPasswordMessage.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            val user = (loginState as LoginState.Success).user
            val route = when (user.role) {
                "owner" -> Screen.Dashboard.route
                "coach" -> Screen.CoachProfile.route // Using existing CoachProfile for Coach Dashboard
                "student" -> Screen.StudentPanel.route
                "parent" -> Screen.ParentPanel.route
                else -> Screen.Dashboard.route
            }
            navController.navigate(route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.email.value = it },
                label = { Text("Email") },
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
            Spacer(modifier = Modifier.height(16.dp))

            if (loginState is LoginState.Error) {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            forgotPasswordMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LaunchedEffect(it) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearMessage()
                }
            }

            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth(),
                enabled = loginState !is LoginState.Loading
            ) {
                if (loginState is LoginState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = { viewModel.forgotPassword() }) {
                Text("Forgot Password?")
            }
            
            TextButton(onClick = { navController.navigate(Screen.RegisterClub.route) }) {
                Text("Register New Club")
            }
        }
    }
}
