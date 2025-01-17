package com.dicoding.membership.view.dashboard.floatingpromo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StaffAddPromoViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val promoUseCase: PromoUseCase
) : ViewModel() {

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun createPromo(
        name: String,
        token: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        merchantId: String,
        maximalUse: Int,
        used: Int,
        isActive: Boolean
    ) = promoUseCase.createPromo(
        name, token, category, detail, pictures, tnc,
        startDate, endDate, memberType, merchantId,
        maximalUse, used, isActive
    ).asLiveData()

    fun editPromo(
        id: String,
        name: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        maximalUse: Int,
        isActive: Boolean
    ) = promoUseCase.editPromo(
        id = id,
        name = name,
        category = category,
        detail = detail,
        pictures = pictures,
        tnc = tnc,
        startDate = startDate,
        endDate = endDate,
        memberType = memberType,
        maximalUse = maximalUse,
        isActive = isActive
    ).asLiveData()
}