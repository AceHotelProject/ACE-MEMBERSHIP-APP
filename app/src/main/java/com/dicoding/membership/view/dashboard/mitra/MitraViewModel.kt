package com.dicoding.membership.view.dashboard.mitra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.merchants.usecase.MerchantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MitraViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val merchantUseCase: MerchantUseCase
) : ViewModel() {

    fun saveMerchantId(id: String) = authUseCase.saveMerchantId(id).asLiveData()

    fun getMerchantId() = authUseCase.getMerchantId().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun getMerchants() = merchantUseCase.getMerchants().cachedIn(viewModelScope)

    fun getMerchantsById(id: String) = merchantUseCase.getMerchantById(id).asLiveData()
}