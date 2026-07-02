package com.sportclubai.presentation.club

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.usecase.RegisterClubUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterClubViewModel @Inject constructor(
    private val registerClubUseCase: RegisterClubUseCase
) : ViewModel() {
    
    private val _currentStep = MutableStateFlow(1)
    val currentStep = _currentStep.asStateFlow()
    
    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val mobileNumber = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    val profilePhotoUri = MutableStateFlow<Uri?>(null)

    val clubName = MutableStateFlow("")
    val sportType = MutableStateFlow("")
    val country = MutableStateFlow("")
    val city = MutableStateFlow("")
    val address = MutableStateFlow("")
    val clubPhone = MutableStateFlow("")
    val website = MutableStateFlow("")
    val clubLogoUri = MutableStateFlow<Uri?>(null)

    val language = MutableStateFlow("English")
    val currency = MutableStateFlow("USD")
    val timeZone = MutableStateFlow("UTC")

    private val _isRegistering = MutableStateFlow(false)
    val isRegistering = _isRegistering.asStateFlow()

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess = _registrationSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun nextStep() {
        if (_currentStep.value < 3) _currentStep.value += 1
    }

    fun previousStep() {
        if (_currentStep.value > 1) _currentStep.value -= 1
    }

    fun registerClub() {
        if (password.value != confirmPassword.value) {
            _errorMessage.value = "Passwords do not match"
            return
        }
        viewModelScope.launch {
            _isRegistering.value = true
            _errorMessage.value = null
            try {
                registerClubUseCase(
                    firstName = firstName.value,
                    lastName = lastName.value,
                    email = email.value,
                    password = password.value,
                    mobileNumber = mobileNumber.value,
                    profilePhotoUri = profilePhotoUri.value,
                    clubName = clubName.value,
                    sportType = sportType.value,
                    country = country.value,
                    city = city.value,
                    address = address.value,
                    clubPhone = clubPhone.value,
                    website = website.value,
                    clubLogoUri = clubLogoUri.value,
                    language = language.value,
                    currency = currency.value,
                    timeZone = timeZone.value
                )
                _registrationSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unknown error occurred"
            } finally {
                _isRegistering.value = false
            }
        }
    }
}
