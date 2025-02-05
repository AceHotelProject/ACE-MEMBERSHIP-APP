package com.dicoding.membership.view.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.test.auth.usecase.AuthUseCaseTester
import com.dicoding.membership.core.domain.auth.tester.model.RegisterResponseDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
//    private val authUseCaseTester: AuthUseCaseTester
    private val authUseCase: AuthUseCase
) : ViewModel() {

    fun register( email: String, password: String, androidId: String) =
        authUseCase.register(email, password, androidId).asLiveData()
}