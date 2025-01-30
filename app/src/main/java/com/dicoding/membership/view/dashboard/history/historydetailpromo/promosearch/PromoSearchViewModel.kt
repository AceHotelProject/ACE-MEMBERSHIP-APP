package com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
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
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

    // Promos flow that combines category and search
    val promos: Flow<PagingData<PromoDomain>> = combine(
        _selectedCategory,
        _selectedStatus,
        _selectedDate,
        _searchQuery
    ) { category, status, date, query ->
        FilterParams(category, status.ifEmpty { "valid" }, date, query)
    }.flatMapLatest { params ->
        promoUseCase.getPromos(
            category = params.category,
            status = params.status,
            name = params.query
        ).map { pagingData ->
            if (params.date.isNotEmpty()) {
                pagingData.filter { promo ->
                    isWithinSelectedDateRange(promo.endDate, params.date)
                }
            } else {
                pagingData
            }
        }
    }.cachedIn(viewModelScope)

    // History flow that combines category and search
    val promoHistory: Flow<PagingData<PromoHistoryDomain>> = combine(
        _selectedCategory,
        _selectedStatus,
        _selectedDate,
        _searchQuery
    ) { category, status, date, query ->
        FilterParams(category, status, date, query)
    }.flatMapLatest { params ->
        promoUseCase.getPromoHistory(
            promoName = params.query,
            promoCategory = params.category,
            status = params.status
        ).map { pagingData ->
            if (params.date.isNotEmpty()) {
                pagingData.filter { history ->
                    isWithinSelectedDateRange(history.activationDate, params.date)
                }
            } else {
                pagingData
            }
        }
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
    private fun isWithinSelectedDateRange(dateString: String, filterType: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            return false
        }

        val currentDate = Calendar.getInstance()
        val targetDate = Calendar.getInstance().apply {
            time = date ?: return false
        }

        return when (filterType) {
            "Hari Ini" -> {
                currentDate.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                        currentDate.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR)
            }
            "Bulan Ini" -> {
                currentDate.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                        currentDate.get(Calendar.MONTH) == targetDate.get(Calendar.MONTH)
            }
            "Tahun Ini" -> {
                currentDate.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR)
            }
            else -> true // Untuk pilihan "All" atau input tidak valid
        }
    }

    private data class FilterParams(
        val category: String = "",
        val status: String = "",
        val date: String = "",
        val query: String = ""
    )
}