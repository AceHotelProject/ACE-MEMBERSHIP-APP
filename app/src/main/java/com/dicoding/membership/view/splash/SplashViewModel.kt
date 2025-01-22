package com.dicoding.membership.view.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.test.auth.usecase.AuthUseCaseTester
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
//    private val authUseCaseTester: AuthUseCaseTester
    private val authUseCase: AuthUseCase
) : ViewModel() {

    fun getUser() = authUseCase.getUser().asLiveData()

    fun getAccessToken() = authUseCase.getAccessToken().asLiveData()

    fun getEmailVerifiedStatus() = authUseCase.getEmailVerifiedStatus().asLiveData()
}