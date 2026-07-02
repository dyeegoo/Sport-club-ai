package com.sportclubai.presentation.training

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateTrainingDialog(
    onDismiss: () -> Unit,
    onGenerate: (
        sport: String,
        belt: String,
        age: Int,
        attendance: Double,
        weak: List<String>,
        strong: List<String>,
        availability: Int,
        notes: String
    ) -> Unit
) {
    var sport by remember { mutableStateOf("Taekwondo") }
    var belt by remember { mutableStateOf("Yellow") }
    var ageStr by remember { mutableStateOf("18") }
    var availabilityStr by remember { mutableStateOf("3") }
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate AI Plan") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = sport,
                    onValueChange = { sport = it },
                    label = { Text("Sport") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = belt,
                    onValueChange = { belt = it },
                    label = { Text("Current Belt") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ageStr,
                    onValueChange = { ageStr = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = availabilityStr,
                    onValueChange = { availabilityStr = it },
                    label = { Text("Days/Week Available") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Coach Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val age = ageStr.toIntOrNull() ?: 18
                val avail = availabilityStr.toIntOrNull() ?: 3
                onGenerate(
                    sport,
                    belt,
                    age,
                    80.0, // Mock attendance
                    listOf("Stamina"), // Mock weak
                    listOf("Kicks"), // Mock strong
                    avail,
                    notes
                )
            }) {
                Text("Generate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
