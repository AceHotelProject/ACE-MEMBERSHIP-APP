package com.dicoding.membership.view.dashboard.promo.detail.detailpromo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PromoDetailViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val promoUseCase: PromoUseCase
) : ViewModel() {

    fun getUser() = authUseCase.getUser().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun deletePromo(id: String) = promoUseCase.deletePromo(id).asLiveData()

    fun activatePromoResepsionis(id: String) = promoUseCase.activatePromoResepsionis(id).asLiveData()

    fun activatePromoUser(id: String) = promoUseCase.activatePromoUser(id).asLiveData()
}