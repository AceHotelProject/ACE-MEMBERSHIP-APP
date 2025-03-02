package com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
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
class PromoSearchViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val promoUseCase: PromoUseCase
) : ViewModel() {

    // Auth related
    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun getUser() = authUseCase.getUser().asLiveData()

    // Category selection
    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Status selection
    private val _selectedStatus = MutableStateFlow("")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    // Date selection
    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val promos: Flow<PagingData<PromoDomain>> = combine(
        _selectedCategory,
        _selectedStatus,
        _selectedDate,
        _searchQuery,
        authUseCase.getUser()
    ) { category, status, date, query, loginDomain ->
        val effectiveStatus = if (status == "Semua") {
            getStatusByUserRole(loginDomain.user.role)
        } else {
            status
        }

        FilterParams(
            category = category,
            status = effectiveStatus,
            date = date,
            query = query
        )
    }.flatMapLatest { params ->
        promoUseCase.getPromos(
            category = params.category,
            status = params.status,
            name = params.query,
            expiredDate = formatDateForQuery(params.date),
            merchantName = params.merchantName
        )
    }.cachedIn(viewModelScope)


    val promoHistory: Flow<PagingData<PromoHistoryDomain>> = combine(
        _selectedCategory,
        _selectedStatus,
        _selectedDate,
        _searchQuery,
        authUseCase.getUser()
    ) { category, status, date, query, loginDomain ->
        val effectiveStatus = if (status == "Semua") {
            getStatusByUserRole(loginDomain.user.role)
        } else {
            status
        }

        FilterParams(
            category = category,
            status = effectiveStatus,
            date = date,
            query = query
        )
    }.flatMapLatest { params ->
        promoUseCase.getPromoHistory(
            name = params.query,
            category = params.category,
            status = params.status,
            expiredDate = formatDateForQuery(params.date)
        )
    }.cachedIn(viewModelScope)

    // Setters
    fun setCategory(category: String) {
        _selectedCategory.value = category
    }
    fun setStatus(status: String) {
        _selectedStatus.value = status
    }
    fun setDate(date: String) {
        _selectedDate.value = date
    }
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

//    Add Time Category
    private fun formatDateForQuery(filterType: String): String {
        return when (filterType) {
            "Hari Ini" -> "today"
            "Minggu Ini" -> "this_week"
            "Bulan Ini" -> "this_month"
            "Tahun Ini" -> "this_year"
            "Semua" -> "" // Empty string for no filter
            else -> "" // Default case
        }
    }

    private fun getStatusByUserRole(role: String): String {
        return when (role) {
            "ADMIN", "MITRA" -> "valid"
            "RECEPTIONIST" -> "draft"
            "MEMBER" -> "active"
            else -> ""
        }
    }
    private data class FilterParams(
        val category: String = "",
        val status: String = "",
        val date: String = "",
        val query: String = "",
        val merchantName: String = ""
    )
}