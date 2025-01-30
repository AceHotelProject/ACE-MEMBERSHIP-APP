package com.dicoding.membership.view.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun sendOtp() =
        authUseCase.sendOtp().asLiveData()

    fun verifyOtp(token: String) =
        authUseCase.verifyOtp(token).asLiveData()

    fun saveEmailVerifiedStatus(isVerified: Boolean) = viewModelScope.launch {
        authUseCase.saveEmailVerifiedStatus(isVerified).collect {  }
    }

    fun getEmailVerifiedStatus() = authUseCase.getEmailVerifiedStatus().asLiveData()
}