package com.dicoding.membership.view.dashboard.promo.detail.detailpromo.detailmitrapromo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.merchants.usecase.MerchantUseCase
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class DetailMitraPromoViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val promoUseCase: PromoUseCase,
    private val merchantUseCase: MerchantUseCase
) : ViewModel() {

    fun getUser() = authUseCase.getUser().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun getMerchantsById(id: String) = merchantUseCase.getMerchantById(id).asLiveData()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _merchantName = MutableStateFlow("")
    val merchantName: StateFlow<String> = _merchantName.asStateFlow()

    val promos: Flow<PagingData<PromoDomain>> = combine(
        _selectedCategory,
        _merchantName
    ) { category, merchantName ->
        Pair(category, merchantName)
    }.flatMapLatest { (category, merchantName) ->
        getPromos(
            category = category,
            status = "valid",
            name = "",
            expiredDate = "",
            merchantName = merchantName
        )
    }.cachedIn(viewModelScope)


    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setMerchantName(name: String) {
        _merchantName.value = name
    }

    fun getPromos(category: String, status: String, name: String, expiredDate: String, merchantName: String) =
        promoUseCase.getPromos(category, status, name, expiredDate, merchantName)
}