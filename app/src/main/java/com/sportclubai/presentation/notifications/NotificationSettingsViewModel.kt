package com.sportclubai.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.usecase.UpdateNotificationPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val updateNotificationPreferencesUseCase: UpdateNotificationPreferencesUseCase
) : ViewModel() {

    private val _preferences = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val preferences = _preferences.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch
                val user = authRepository.getUser(uid)
                if (user != null) {
                    _preferences.value = user.notificationPreferences
                }
            } catch (e: Exception) {
                // Ignore
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePreference(key: String, value: Boolean) {
        val newPrefs = _preferences.value.toMutableMap()
        newPrefs[key] = value
        _preferences.value = newPrefs
        viewModelScope.launch {
            try {
                updateNotificationPreferencesUseCase(newPrefs)
            } catch (e: Exception) {
                // Revert if failed
                loadPreferences()
            }
        }
    }
}
