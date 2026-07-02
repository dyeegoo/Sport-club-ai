package com.sportclubai.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.usecase.LoginUseCase
import com.sportclubai.domain.usecase.ForgotPasswordUseCase
import com.sportclubai.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()
    
    private val _forgotPasswordMessage = MutableStateFlow<String?>(null)
    val forgotPasswordMessage = _forgotPasswordMessage.asStateFlow()

    fun login() {
        if (email.value.isBlank() || password.value.isBlank()) {
            _loginState.value = LoginState.Error("Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val user = loginUseCase(email.value, password.value)
                _loginState.value = LoginState.Success(user)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Login failed")
            }
        }
    }
    
    fun forgotPassword() {
        if (email.value.isBlank()) {
            _forgotPasswordMessage.value = "Please enter your email to reset password"
            return
        }
        viewModelScope.launch {
            try {
                forgotPasswordUseCase(email.value)
                _forgotPasswordMessage.value = "Password reset email sent."
            } catch (e: Exception) {
                _forgotPasswordMessage.value = e.message ?: "Failed to send reset email."
            }
        }
    }
    
    fun clearMessage() {
        _forgotPasswordMessage.value = null
    }
}
