package com.sportclubai.presentation.student

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.sportclubai.presentation.training.AITrainingDashboardScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MyTrainingScreen(navController: NavController) {
    var studentId by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        studentId = FirebaseAuth.getInstance().currentUser?.uid
    }
    
    if (studentId != null) {
        AITrainingDashboardScreen(navController = navController, studentId = studentId)
    }
}
