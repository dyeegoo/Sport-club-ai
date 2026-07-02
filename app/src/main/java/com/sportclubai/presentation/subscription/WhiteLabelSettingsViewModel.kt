package com.sportclubai.presentation.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.ClubBranding
import com.sportclubai.domain.model.FeatureFlag
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.WhiteLabelRepository
import com.sportclubai.domain.usecase.CheckFeatureAccessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WhiteLabelState {
    object Loading : WhiteLabelState()
    data class Success(val branding: ClubBranding, val hasAccess: Boolean) : WhiteLabelState()
    data class Error(val message: String) : WhiteLabelState()
}

@HiltViewModel
class WhiteLabelSettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val whiteLabelRepository: WhiteLabelRepository,
    private val checkFeatureAccessUseCase: CheckFeatureAccessUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WhiteLabelState>(WhiteLabelState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadBranding()
    }

    fun loadBranding() {
        viewModelScope.launch {
            _uiState.value = WhiteLabelState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                val hasAccess = checkFeatureAccessUseCase(user.clubId, FeatureFlag.WHITE_LABEL)
                val branding = whiteLabelRepository.getClubBranding(user.clubId).firstOrNull() ?: ClubBranding(clubId = user.clubId)
                
                _uiState.value = WhiteLabelState.Success(branding, hasAccess)
            } catch (e: Exception) {
                _uiState.value = WhiteLabelState.Error(e.message ?: "Failed to load settings")
            }
        }
    }

    fun saveBranding(branding: ClubBranding) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is WhiteLabelState.Success && currentState.hasAccess) {
                    whiteLabelRepository.updateClubBranding(branding)
                    _uiState.value = currentState.copy(branding = branding)
                }
            } catch (e: Exception) {
                // Ignore or show error
            }
        }
    }
}
