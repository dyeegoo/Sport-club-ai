package com.sportclubai.presentation.audit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.AuditLog
import com.sportclubai.domain.repository.AuditLogRepository
import com.sportclubai.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuditLogState {
    object Loading : AuditLogState()
    data class Success(val logs: List<AuditLog>) : AuditLogState()
    data class Error(val message: String) : AuditLogState()
}

@HiltViewModel
class AuditLogViewModel @Inject constructor(
    private val auditLogRepository: AuditLogRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuditLogState>(AuditLogState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadLogs()
    }

    fun loadLogs() {
        viewModelScope.launch {
            _uiState.value = AuditLogState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                auditLogRepository.getAuditLogs(user.clubId)
                    .catch { e -> _uiState.value = AuditLogState.Error(e.message ?: "Failed to load logs") }
                    .collect { logs ->
                        _uiState.value = AuditLogState.Success(logs)
                    }
            } catch (e: Exception) {
                _uiState.value = AuditLogState.Error(e.message ?: "Failed to load logs")
            }
        }
    }
}
