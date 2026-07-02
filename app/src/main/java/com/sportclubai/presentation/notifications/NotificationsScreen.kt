package com.sportclubai.presentation.notifications

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.Notification
import com.sportclubai.domain.model.NotificationType
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    
    // Additional state for filtering/searching could be added here
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(Icons.Default.Check, contentDescription = "Mark All As Read")
                    }
                    IconButton(onClick = { navController.navigate("notification_settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is NotificationsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NotificationsState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadNotifications() }) {
                            Text("Retry")
                        }
                    }
                }
                is NotificationsState.Success -> {
                    val notifications = state.notifications.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.body.contains(searchQuery, ignoreCase = true)
                    }
                    val unreadCount = state.notifications.count { !it.readBy.contains(currentUserId) }
                    
                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search notifications") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            }
                        )

                        if (unreadCount > 0) {
                            Text(
                                text = "You have $unreadCount unread notification(s)",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (notifications.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No notifications found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            NotificationsList(
                                notifications = notifications,
                                currentUserId = currentUserId,
                                onNotificationClick = { notification ->
                                    viewModel.markAsRead(notification)
                                    // Navigate based on type
                                    when (notification.type) {
                                        NotificationType.NEW_MESSAGE -> navController.navigate("messages")
                                        NotificationType.PAYMENT_REMINDER -> navController.navigate("payments")
                                        NotificationType.ATTENDANCE_WARNING -> navController.navigate("attendance")
                                        else -> navController.navigate("notification_detail/${notification.notificationId}")
                                    }
                                },
                                onDelete = { notification ->
                                    viewModel.deleteNotification(notification.notificationId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsList(
    notifications: List<Notification>,
    currentUserId: String,
    onNotificationClick: (Notification) -> Unit,
    onDelete: (Notification) -> Unit
) {
    val unread = notifications.filter { !it.readBy.contains(currentUserId) }
    val read = notifications.filter { it.readBy.contains(currentUserId) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (unread.isNotEmpty()) {
            item {
                Text(
                    text = "Unread",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(unread, key = { it.notificationId }) { notification ->
                NotificationItem(
                    notification = notification,
                    isUnread = true,
                    onClick = { onNotificationClick(notification) },
                    onDelete = { onDelete(notification) }
                )
            }
        }
        
        if (read.isNotEmpty()) {
            item {
                Text(
                    text = "Earlier",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            items(read, key = { it.notificationId }) { notification ->
                NotificationItem(
                    notification = notification,
                    isUnread = false,
                    onClick = { onNotificationClick(notification) },
                    onDelete = { onDelete(notification) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: Notification,
    isUnread: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = if (isUnread) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NotificationIcon(notification.type)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (isUnread) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notification.body,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = DateUtils.getRelativeTimeSpanString(notification.createdAt).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationIcon(type: NotificationType) {
    val icon = when (type) {
        NotificationType.NEW_MESSAGE -> Icons.Default.Email
        NotificationType.PAYMENT_REMINDER -> Icons.Default.ShoppingCart
        NotificationType.ATTENDANCE_WARNING -> Icons.Default.Warning
        NotificationType.UPCOMING_CLASS -> Icons.Default.DateRange
        NotificationType.CLASS_CANCELLED -> Icons.Default.Close
        NotificationType.BELT_EXAM_REMINDER -> Icons.Default.Star
        NotificationType.BELT_EXAM_RESULT -> Icons.Default.CheckCircle
        NotificationType.COACH_ANNOUNCEMENT -> Icons.Default.Notifications
        NotificationType.CLUB_ANNOUNCEMENT -> Icons.Default.Notifications
        NotificationType.NEW_STUDENT_REGISTRATION -> Icons.Default.Person
        NotificationType.AI_RECOMMENDATION -> Icons.Default.Info
        NotificationType.SYSTEM -> Icons.Default.Settings
    }
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, shape = androidx.compose.foundation.shape.CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}