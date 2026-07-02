package com.sportclubai.presentation.security

import androidx.lifecycle.ViewModel
import com.sportclubai.core.security.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityManager: SecurityManager
) : ViewModel() {

    private val _securityViolations = MutableStateFlow<List<String>>(emptyList())
    val securityViolations = _securityViolations.asStateFlow()

    init {
        checkEnvironment()
    }

    private fun checkEnvironment() {
        _securityViolations.value = securityManager.checkSecurityViolations()
    }
}
