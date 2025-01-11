package com.dicoding.membership.view.dashboard.floatingcoupon.reedemcoupon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RedeemCouponCodeViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val promoUseCase: PromoUseCase
) : ViewModel() {

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun redeemPromo(token: String) =
        promoUseCase.redeemPromo(token).asLiveData()
}