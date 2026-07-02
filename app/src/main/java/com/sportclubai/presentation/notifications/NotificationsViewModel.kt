package com.sportclubai.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Notification
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.usecase.DeleteNotificationUseCase
import com.sportclubai.domain.usecase.GetNotificationsUseCase
import com.sportclubai.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.sportclubai.domain.usecase.MarkNotificationAsReadUseCase
import com.sportclubai.domain.usecase.BulkDeleteNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NotificationsState {
    object Loading : NotificationsState()
    data class Success(val notifications: List<Notification>) : NotificationsState()
    data class Error(val message: String) : NotificationsState()
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val bulkDeleteNotificationsUseCase: BulkDeleteNotificationsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationsState>(NotificationsState.Loading)
    val uiState = _uiState.asStateFlow()

    val currentUserId = MutableStateFlow("")
    val userRole = MutableStateFlow("")

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationsState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: ""
                currentUserId.value = uid
                val user = authRepository.getUser(uid)
                userRole.value = user?.role ?: ""

                getNotificationsUseCase()
                    .catch { e ->
                        _uiState.value = NotificationsState.Error(e.message ?: "Failed to load notifications")
                    }
                    .collect { notifications ->
                        _uiState.value = NotificationsState.Success(notifications)
                    }
            } catch (e: Exception) {
                _uiState.value = NotificationsState.Error(e.message ?: "Failed to load notifications")
            }
        }
    }

    fun markAsRead(notification: Notification) {
        if (!notification.readBy.contains(currentUserId.value)) {
            viewModelScope.launch {
                try {
                    markNotificationAsReadUseCase(notification.notificationId)
                } catch (e: Exception) {
                    // Ignore errors for read status
                }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                markAllNotificationsAsReadUseCase()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                deleteNotificationUseCase(notificationId)
            } catch (e: Exception) {
                // Ignore delete errors or show toast
            }
        }
    }

    fun bulkDelete(notificationIds: List<String>) {
        viewModelScope.launch {
            try {
                bulkDeleteNotificationsUseCase(notificationIds)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
