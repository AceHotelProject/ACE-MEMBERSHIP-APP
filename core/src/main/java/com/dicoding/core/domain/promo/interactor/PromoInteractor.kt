package com.dicoding.core.domain.promo.interactor

import androidx.paging.PagingData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.promo.model.ActivatePromoDomain
import com.dicoding.core.domain.promo.model.GetPromosDomain
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.domain.promo.model.RedeemPromoDomain
import com.dicoding.core.domain.promo.repository.IPromoRepository
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PromoInteractor @Inject constructor(
    private val promoRepository: IPromoRepository
) : PromoUseCase {

    override fun createPromo(
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
    ): Flow<Resource<PromoDomain>> =
        promoRepository.createPromo(
            name, token, category, detail, pictures, tnc,
            startDate, endDate, memberType, merchantId,
            maximalUse, used, isActive
        )

    override fun getPromos(): Flow<PagingData<PromoDomain>> =
        promoRepository.getPromos()

    override fun getProposalPromos(): Flow<Resource<GetPromosDomain>> =
        promoRepository.getProposalPromos()

    override fun editPromo(
        id: String,
        token: String,
        name: String?,
        category: String?,
        detail: String?,
        pictures: List<String>?,
        tnc: List<String>?,
        startDate: String?,
        endDate: String?,
        memberType: String?,
        maximalUse: Int?,
        isActive: Boolean?
    ): Flow<Resource<PromoDomain>> =
        promoRepository.editPromo(
            id, token, name, category, detail, pictures, tnc,
            startDate, endDate, memberType, maximalUse, isActive
        )

    override fun deletePromo(id: String): Flow<Resource<Unit>> =
        promoRepository.deletePromo(id)

    override fun activatePromo(id: String): Flow<Resource<ActivatePromoDomain>> =
        promoRepository.activatePromo(id)

    override fun redeemPromo(token: String): Flow<Resource<Unit>> =
        promoRepository.redeemPromo(token)

    override fun getPromoHistory(): Flow<Resource<List<PromoHistoryDomain>>> =
        promoRepository.getPromoHistory()
}