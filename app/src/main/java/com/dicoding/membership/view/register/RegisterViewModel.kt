package com.dicoding.membership.view.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.tester.usecase.AuthUseCaseTester
import com.dicoding.membership.core.domain.auth.tester.model.RegisterResponseDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCaseTester: AuthUseCaseTester
) : ViewModel() {

    private val _registerResult = MutableStateFlow<Resource<RegisterResponseDomain>>(Resource.Loading())
    val registerResult: StateFlow<Resource<RegisterResponseDomain>> = _registerResult

    fun register(name: String, email: String, password: String) =
        authUseCaseTester.registerUser(name, email, password).asLiveData()

}