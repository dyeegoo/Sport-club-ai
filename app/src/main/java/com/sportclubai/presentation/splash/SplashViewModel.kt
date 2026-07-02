package com.sportclubai.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.usecase.CheckSessionUseCase
import com.sportclubai.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashState {
    object Loading : SplashState()
    data class Authenticated(val user: User) : SplashState()
    object Unauthenticated : SplashState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState = _splashState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                val user = checkSessionUseCase()
                if (user != null) {
                    _splashState.value = SplashState.Authenticated(user)
                } else {
                    _splashState.value = SplashState.Unauthenticated
                }
            } catch (e: Exception) {
                _splashState.value = SplashState.Unauthenticated
            }
        }
    }
}
