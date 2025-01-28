package com.dicoding.membership.view.dashboard.admin.manajemenmitra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.merchants.usecase.MerchantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManajemenMitraViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val merchantUseCase: MerchantUseCase
) : ViewModel() {

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun getMerchants() = merchantUseCase.getMerchants().cachedIn(viewModelScope)

    fun deleteMerchant(id: String) = merchantUseCase.deleteMerchant(id).asLiveData()

    fun saveMerchantId(id: String) = authUseCase.saveMerchantId(id).asLiveData()
}