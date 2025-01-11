package com.dicoding.core.domain.promo.repository

import androidx.paging.PagingData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.promo.model.ActivatePromoDomain
import com.dicoding.core.domain.promo.model.GetPromosDomain
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.domain.promo.model.RedeemPromoDomain
import kotlinx.coroutines.flow.Flow

interface IPromoRepository {
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
    ): Flow<Resource<PromoDomain>>

    fun getPromos(): Flow<PagingData<PromoDomain>>

    fun getProposalPromos(): Flow<Resource<GetPromosDomain>>

    fun editPromo(
        id: String,
        token: String,
        name: String? = null,
        category: String? = null,
        detail: String? = null,
        pictures: List<String>? = null,
        tnc: List<String>? = null,
        startDate: String? = null,
        endDate: String? = null,
        memberType: String? = null,
        maximalUse: Int? = null,
        isActive: Boolean? = null
    ): Flow<Resource<PromoDomain>>

    fun deletePromo(id: String): Flow<Resource<Unit>>

    fun activatePromo(id: String): Flow<Resource<ActivatePromoDomain>>

    fun redeemPromo(token: String): Flow<Resource<Unit>>

    fun getPromoHistory(): Flow<Resource<List<PromoHistoryDomain>>>
}