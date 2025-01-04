package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.remote.response.promo.ActivatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.CreatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.DeletePromoResponse
import com.dicoding.core.data.source.remote.response.promo.EditPromoResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoHistoryResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoResponse
import com.dicoding.core.data.source.remote.response.promo.RedeemPromoResponse
import com.dicoding.core.domain.promo.interactor.ActivatePromoDomain
import com.dicoding.core.domain.promo.model.DeletePromoDomain
import com.dicoding.core.domain.promo.model.GetPromoHistoryDomain
import com.dicoding.core.domain.promo.model.GetPromosDomain
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.domain.promo.model.RedeemPromoDomain

object PromoDataMapper {
    fun mapCreatePromoResponseToDomain(input: CreatePromoResponse): PromoDomain {
        return PromoDomain(
            id = input.id ?: "",
            name = input.name ?: "",
            category = input.category ?: "",
            detail = input.detail ?: "",
            pictures = input.pictures?.filterNotNull() ?: emptyList(),
            tnc = input.tnc?.filterNotNull() ?: emptyList(),
            startDate = input.startDate ?: "",
            endDate = input.endDate ?: "",
            memberType = input.memberType ?: "",
            merchantId = input.merchantId ?: "",
            maximalUse = input.maximalUse ?: 0,
            used = input.used ?: 0,
            isActive = input.isActive ?: false,
            createdBy = input.createdBy,
            updatedBy = input.updatedBy,
            token = input.tokenId
        )
    }

    fun mapGetPromoResponseToDomain(input: GetPromoResponse): GetPromosDomain {
        return GetPromosDomain(
            totalResults = input.totalResults ?: 0,
            limit = input.limit ?: 0,
            totalPages = input.totalPages ?: 0,
            page = input.page ?: 0,
            results = input.results?.filterNotNull()?.map {
                PromoDomain(
                    id = it.id ?: "",
                    name = it.name ?: "",
                    category = it.category ?: "",
                    detail = it.detail ?: "",
                    pictures = it.pictures?.filterNotNull() ?: emptyList(),
                    tnc = it.tnc?.filterNotNull() ?: emptyList(),
                    startDate = it.startDate ?: "",
                    endDate = it.endDate ?: "",
                    memberType = it.memberType ?: "",
                    merchantId = it.merchantId ?: "",
                    maximalUse = it.maximalUse ?: 0,
                    used = it.used ?: 0,
                    isActive = it.isActive ?: false,
                    createdBy = it.createdBy,
                    updatedBy = it.updatedBy,
                    token = it.token
                )
            } ?: emptyList()
        )
    }

    fun mapEditPromoResponseToDomain(input: EditPromoResponse): PromoDomain {
        return PromoDomain(
            id = input.id ?: "",
            name = input.name ?: "",
            category = input.category ?: "",
            detail = input.detail ?: "",
            pictures = input.pictures?.filterNotNull() ?: emptyList(),
            tnc = input.tnc?.filterNotNull() ?: emptyList(),
            startDate = input.startDate ?: "",
            endDate = input.endDate ?: "",
            memberType = input.memberType ?: "",
            merchantId = input.merchantId ?: "",
            maximalUse = input.maximalUse ?: 0,
            used = input.used ?: 0,
            isActive = input.isActive ?: false,
            createdBy = input.createdBy,
            updatedBy = input.updatedBy,
            token = input.token
        )
    }

    fun mapDeletePromoResponseToDomain(input: DeletePromoResponse): DeletePromoDomain {
        return DeletePromoDomain(
            success = input.success ?: false,
            message = input.message ?: "",
            id = input.id ?: ""
        )
    }

    fun mapRedeemPromoResponseToDomain(input: RedeemPromoResponse): RedeemPromoDomain {
        return RedeemPromoDomain(
            success = input.success ?: false,
            message = input.message ?: "",
            promoId = input.promoId ?: "",
            redeemDate = input.redeemDate ?: "",
            userId = input.userId ?: ""
        )
    }

    fun mapGetPromoHistoryResponseToDomain(input: GetPromoHistoryResponse): GetPromoHistoryDomain {
        return GetPromoHistoryDomain(
            totalResults = input.totalResults ?: 0,
            limit = input.limit ?: 0,
            totalPages = input.totalPages ?: 0,
            page = input.page ?: 0,
            results = input.results?.filterNotNull()?.map {
                PromoHistoryDomain(
                    promoId = it.promoId ?: "",
                    redeemDate = it.redeemDate ?: "",
                    userId = it.userId ?: "",
                    promoName = it.promoName ?: "",
                    status = it.status ?: ""
                )
            } ?: emptyList()
        )
    }

    fun mapActivatePromoResponseToDomain(input: ActivatePromoResponse): ActivatePromoDomain {
        return ActivatePromoDomain(
            expires = input.expires ?: "",
            blacklisted = input.blacklisted ?: false,
            id = input.id ?: "",
            type = input.type ?: "",
            user = input.user ?: "",
            token = input.token ?: ""
        )
    }
}
