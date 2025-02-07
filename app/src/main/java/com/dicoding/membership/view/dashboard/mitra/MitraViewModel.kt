package com.dicoding.membership.view.dashboard.mitra

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
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MitraViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val promoUseCase: PromoUseCase,
    private val merchantUseCase: MerchantUseCase
) : ViewModel() {

    fun saveMerchantId(id: String) = authUseCase.saveMerchantId(id).asLiveData()

    fun getMerchantId() = authUseCase.getMerchantId().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun getMerchants() = merchantUseCase.getMerchants().cachedIn(viewModelScope)

    fun getMerchantsById(id: String) = merchantUseCase.getMerchantById(id).asLiveData()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val promos: Flow<PagingData<PromoDomain>> = _selectedCategory
        .flatMapLatest { category ->
            getPromos(
                category = category,
                status = "valid",
                name = ""
            )
        }
        .cachedIn(viewModelScope)

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun getPromos(category: String, status: String, name: String) =
        promoUseCase.getPromos(category, status, name)
}