package com.sportclubai.presentation.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ExportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val url: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val exportRepository: ExportRepository
) : ViewModel() {

    private val _exportStatus = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportStatus = _exportStatus.asStateFlow()

    fun requestExport(type: String, format: String) {
        viewModelScope.launch {
            _exportStatus.value = ExportState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                val result = exportRepository.requestExport(user.clubId, type, format)
                if (result.isSuccess) {
                    val exportId = result.getOrNull()!!
                    exportRepository.getExportStatus(user.clubId, exportId).collect { url ->
                        _exportStatus.value = ExportState.Success(url)
                    }
                } else {
                    _exportStatus.value = ExportState.Error(result.exceptionOrNull()?.message ?: "Export failed")
                }
            } catch (e: Exception) {
                _exportStatus.value = ExportState.Error(e.message ?: "Export failed")
            }
        }
    }
}
