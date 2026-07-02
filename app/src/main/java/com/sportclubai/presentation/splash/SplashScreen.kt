package com.sportclubai.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.core.navigation.Screen

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()

    LaunchedEffect(splashState) {
        when (splashState) {
            is SplashState.Authenticated -> {
                val user = (splashState as SplashState.Authenticated).user
                val route = when (user.role) {
                    "owner" -> Screen.Dashboard.route
                    "coach" -> Screen.CoachProfile.route // Using existing CoachProfile screen for Coach Dashboard
                    "student" -> Screen.StudentPanel.route
                    "parent" -> Screen.ParentPanel.route
                    else -> Screen.Dashboard.route
                }
                navController.navigate(route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            is SplashState.Unauthenticated -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            SplashState.Loading -> { }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
    }
}
