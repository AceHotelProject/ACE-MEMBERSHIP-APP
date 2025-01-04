package com.dicoding.membership.view.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    fun sendOtp(id: String) =
        authUseCase.sendOtp(id).asLiveData()

    fun verifyOtp(id:String, token: Int) =
        authUseCase.verifyOtp(id, token).asLiveData()
}