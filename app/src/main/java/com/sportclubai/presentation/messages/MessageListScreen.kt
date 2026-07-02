package com.sportclubai.presentation.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(
    navController: NavController,
    viewModel: MessageListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    var showNewMessageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
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
            FloatingActionButton(onClick = { showNewMessageDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "New Message")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is MessageListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MessageListState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadData() }) {
                            Text("Retry")
                        }
                    }
                }
                is MessageListState.Success -> {
                    if (state.conversations.isEmpty()) {
                        Text("No messages yet.", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.conversations) { item ->
                                ConversationItemCard(item = item) {
                                    navController.navigate("chat/${item.type}/${item.id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewMessageDialog) {
        AlertDialog(
            onDismissRequest = { showNewMessageDialog = false },
            title = { Text("New Message") },
            text = {
                Column {
                    Text("Select a conversation type to start:")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { 
                        showNewMessageDialog = false
                        navController.navigate("users_list_for_chat") // Placeholder for future enhancement
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Message a User")
                    }
                    if (userRole == "owner" || userRole == "coach") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { 
                            showNewMessageDialog = false
                            navController.navigate("chat/ANNOUNCEMENT/announcements") 
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("Club Announcements")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNewMessageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ConversationItemCard(item: ConversationItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val icon = if (item.type == "ANNOUNCEMENT") Icons.Default.Notifications else Icons.Default.Person
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    Text(sdf.format(Date(item.lastMessageTime)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.lastMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (item.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (item.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (item.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.error,
                            shape = CircleShape,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.unreadCount.toString(), color = MaterialTheme.colorScheme.onError, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
