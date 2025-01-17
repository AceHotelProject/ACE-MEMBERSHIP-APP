package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.remote.response.promo.ActivatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.CreatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.DeletePromoResponse
import com.dicoding.core.data.source.remote.response.promo.EditPromoResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoHistoryResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoResponse
import com.dicoding.core.data.source.remote.response.promo.RedeemPromoResponse
import com.dicoding.core.domain.promo.model.ActivatePromoDomain
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
            merchantId = input.merchant ?: "",  // perhatikan perubahan dari merchantId ke merchant
            maximalUse = input.maximalUse ?: 0,
            used = input.used ?: 0,
            isActive = input.isActive ?: false,
            createdBy = input.createdBy,        // tetap nullable
            updatedBy = input.updatedBy,        // tetap nullable
            token = null                        // tidak ada di response
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

    fun mapGetPromoHistoryResponseToDomain(response: GetPromoHistoryResponse): GetPromoHistoryDomain {
        return GetPromoHistoryDomain(
            totalResults = response.totalResults ?: 0,
            limit = response.limit ?: 10,
            totalPages = response.totalPages ?: 0,
            page = response.page ?: 1,
            results = response.results?.mapNotNull { item ->
                item?.let {
                    PromoHistoryDomain(
                        promoName = item.promoName ?: "",
                        promoCategory = item.promoCategory ?: "",
                        promoDetail = item.promoDetail ?: "",
                        promoPictures = item.promoPictures ?: emptyList(),
                        promoTnc = item.promoTnc ?: emptyList(),
                        promoMemberType = item.promoMemberType ?: "",
                        userName = item.userName ?: "Anonymous",
                        tokenCode = item.tokenCode ?: "",
                        merchantName = item.merchantName ?: "Unknown Merchant",
                        activationDate = item.activationDate ?: "",
                        status = item.status ?: "",
                        id = item.id ?: ""
                    )
                }
            } ?: emptyList()
        )
    }

    fun mapActivatePromoResponseToDomain(input: ActivatePromoResponse): ActivatePromoDomain {
        return ActivatePromoDomain(
            promoPictures = input.promoPictures?.filterNotNull() ?: emptyList(),
            activationDate = input.activationDate ?: "",
            promoDetail = input.promoDetail ?: "",
            userName = input.userName ?: "",
            promoName = input.promoName ?: "",
            promoCategory = input.promoCategory ?: "",
            merchantName = input.merchantName ?: "",
            merchant = input.merchant ?: "",
            promoTnc = input.promoTnc?.filterNotNull() ?: emptyList(),
            token = input.token ?: "",
            promo = input.promo ?: "",
            tokenCode = input.tokenCode ?: "",
            id = input.id ?: "",
            promoMemberType = input.promoMemberType ?: "",
            user = input.user ?: "",
            status = input.status ?: ""
        )
    }
}
