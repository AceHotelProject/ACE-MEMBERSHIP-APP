package com.dicoding.membership.view.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.test.auth.usecase.AuthUseCaseTester
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : ViewModel() {

    fun getUser() = authUseCase.getUser().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()
}