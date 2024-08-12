package com.dicoding.membership.view.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.tester.usecase.AuthUseCaseTester
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCaseTester: AuthUseCaseTester,
) : ViewModel() {

    fun getLoginStatus() = authUseCaseTester.getLoginStatus().asLiveData()
}