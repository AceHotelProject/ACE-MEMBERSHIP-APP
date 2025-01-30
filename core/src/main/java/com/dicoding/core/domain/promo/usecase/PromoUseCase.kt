package com.dicoding.core.domain.promo.usecase

import androidx.paging.PagingData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.promo.model.ActivatePromoResepsionisDomain
import com.dicoding.core.domain.promo.model.ActivatePromoUserDomain
import com.dicoding.core.domain.promo.model.GetPromosDomain
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import kotlinx.coroutines.flow.Flow

interface PromoUseCase {
    fun createPromo(
        name: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        maximalUse: Int,
    ): Flow<Resource<PromoDomain>>

    fun getPromos(
        category: String,
        status: String,
        name: String
    ): Flow<PagingData<PromoDomain>>

    fun getProposalPromos(): Flow<Resource<GetPromosDomain>>

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
    ): Flow<Resource<PromoDomain>>

    fun deletePromo(id: String): Flow<Resource<Unit>>

    fun activatePromoResepsionis(id: String): Flow<Resource<ActivatePromoResepsionisDomain>>

    fun activatePromoUser(id: String): Flow<Resource<ActivatePromoUserDomain>>

    fun redeemPromo(token: String): Flow<Resource<Unit>>

    fun getPromoHistory(
        promoName: String,
        promoCategory: String,
        status: String
    ): Flow<PagingData<PromoHistoryDomain>>
}