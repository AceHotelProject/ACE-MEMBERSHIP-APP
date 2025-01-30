package com.dicoding.core.domain.promo.interactor

import androidx.paging.PagingData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.promo.model.ActivatePromoResepsionisDomain
import com.dicoding.core.domain.promo.model.ActivatePromoUserDomain
import com.dicoding.core.domain.promo.model.GetPromosDomain
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.domain.promo.repository.IPromoRepository
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PromoInteractor @Inject constructor(
    private val promoRepository: IPromoRepository
) : PromoUseCase {

    override fun createPromo(
        name: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        maximalUse: Int,
    ): Flow<Resource<PromoDomain>> =
        promoRepository.createPromo(
            name, category, detail, pictures, tnc,
            startDate, endDate, memberType,
            maximalUse
        )

    override fun getPromos(
        category: String,
        status: String,
        name: String
    ): Flow<PagingData<PromoDomain>> =
        promoRepository.getPromos(category, status, name)

    override fun getProposalPromos(): Flow<Resource<GetPromosDomain>> =
        promoRepository.getProposalPromos()

    override fun editPromo(
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
    ): Flow<Resource<PromoDomain>> =
        promoRepository.editPromo(
            id, name, category, detail, pictures, tnc,
            startDate, endDate, memberType, maximalUse, isActive
        )

    override fun deletePromo(id: String): Flow<Resource<Unit>> =
        promoRepository.deletePromo(id)

    override fun activatePromoResepsionis(id: String): Flow<Resource<ActivatePromoResepsionisDomain>> =
        promoRepository.activatePromoResepsionis(id)

    override fun activatePromoUser(id: String): Flow<Resource<ActivatePromoUserDomain>> =
        promoRepository.activatePromoUser(id)

    override fun redeemPromo(token: String): Flow<Resource<Unit>> =
        promoRepository.redeemPromo(token)

    override fun getPromoHistory(
        promoName: String,
        promoCategory: String,
        status: String
    ): Flow<PagingData<PromoHistoryDomain>> =
        promoRepository.getPromoHistory(promoName, promoCategory, status)
}