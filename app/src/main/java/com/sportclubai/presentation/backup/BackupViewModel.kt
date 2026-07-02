package com.sportclubai.presentation.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.BackupMetadata
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BackupState {
    object Loading : BackupState()
    data class Success(val backups: List<BackupMetadata>) : BackupState()
    data class Error(val message: String) : BackupState()
}

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BackupState>(BackupState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadBackups()
    }

    private fun loadBackups() {
        viewModelScope.launch {
            _uiState.value = BackupState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                backupRepository.getBackupHistory(user.clubId)
                    .catch { e -> _uiState.value = BackupState.Error(e.message ?: "Failed to load backups") }
                    .collect { backups ->
                        _uiState.value = BackupState.Success(backups)
                    }
            } catch (e: Exception) {
                _uiState.value = BackupState.Error(e.message ?: "Failed to load backups")
            }
        }
    }

    fun triggerBackup() {
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch
                val user = authRepository.getUser(uid) ?: return@launch
                backupRepository.triggerManualBackup(user.clubId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun restoreBackup(backupId: String) {
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch
                val user = authRepository.getUser(uid) ?: return@launch
                backupRepository.restoreBackup(user.clubId, backupId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
