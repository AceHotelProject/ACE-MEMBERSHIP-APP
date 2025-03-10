package com.dicoding.membership.view.popup.token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.test.auth.usecase.AuthUseCaseTester
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokenExpiredViewModel @Inject constructor(
    private val authUseCaseTester: AuthUseCaseTester,
    private val authUseCase: AuthUseCase
) :
    ViewModel() {

    fun deleteToken() = viewModelScope.launch {
        authUseCaseTester.deleteToken()
    }

    fun getUser() = authUseCase.getUser().asLiveData()

    fun deleteUser(user: LoginDomain) = viewModelScope.launch {
        authUseCase.deleteUser(user)
    }

    fun deleteAllData() = viewModelScope.launch {
        authUseCase.deleteAllData()
    }
}