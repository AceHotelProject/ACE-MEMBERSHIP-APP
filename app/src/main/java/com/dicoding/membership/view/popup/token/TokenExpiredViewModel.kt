package com.dicoding.membership.view.popup.token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.domain.test.auth.usecase.AuthUseCaseTester
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokenExpiredViewModel @Inject constructor(
    private val authUseCase: AuthUseCaseTester
) :
    ViewModel() {

    fun deleteToken() = viewModelScope.launch {
        authUseCase.deleteToken()
    }
}