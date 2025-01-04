package com.dicoding.membership.view.dashboard.mitra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MitraViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : ViewModel() {

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()
}