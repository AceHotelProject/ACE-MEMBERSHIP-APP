package com.dicoding.membership.view.dashboard.admin.detailmitra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.merchants.usecase.MerchantUseCase
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailMitraViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val merchantUseCase: MerchantUseCase,
    private val promoUseCase: PromoUseCase
) : ViewModel() {

    fun getUser() = authUseCase.getUser().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun getMerchantById(id: String) = merchantUseCase.getMerchantById(id).asLiveData()

//    fun getPromos() = promoUseCase.getPromos().cachedIn(viewModelScope)
}