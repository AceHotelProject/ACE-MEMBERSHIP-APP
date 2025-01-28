package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantResponse
import com.dicoding.core.data.source.remote.response.merchants.GetMerchantsByIdResponse
import com.dicoding.core.data.source.remote.response.merchants.GetMerchantsResponse
import com.dicoding.core.data.source.remote.response.merchants.Merchant
import com.dicoding.core.data.source.remote.response.merchants.Owner
import com.dicoding.core.data.source.remote.response.merchants.Receptionist
import com.dicoding.core.data.source.remote.response.merchants.UpdateMerchantResponse
import com.dicoding.core.domain.merchants.model.CreateMerchantDomain
import com.dicoding.core.domain.merchants.model.GetMerchantByIdDomain
import com.dicoding.core.domain.merchants.model.GetMerchantsDomain
import com.dicoding.core.domain.merchants.model.MerchantDomain
import com.dicoding.core.domain.merchants.model.MerchantResultDomain
import com.dicoding.core.domain.merchants.model.OwnerDomain
import com.dicoding.core.domain.merchants.model.ReceptionistDomain
import com.dicoding.core.domain.merchants.model.UpdateMerchantDomain

object MerchantDataMapper {
    fun mapCreateMerchantResponseToDomain(response: CreateMerchantResponse): CreateMerchantDomain {
        return CreateMerchantDomain(
            owner = mapOwnerToDomain(response.owner),
            receptionist = mapReceptionistToDomain(response.receptionist),
            merchant = mapMerchantToDomain(response.merchant)
        )
    }

    private fun mapOwnerToDomain(owner: Owner?): OwnerDomain {
        return OwnerDomain(
            role = owner?.role.orEmpty(),
            isPhoneVerified = owner?.isPhoneVerified ?: false,
            address = owner?.address?.toString(),
            subscriptionEndDate = owner?.subscriptionEndDate?.toString(),
            isNumberVerified = owner?.isNumberVerified ?: false,
            isEmailVerified = owner?.isEmailVerified ?: false,
            point = owner?.point ?: 0,
            pathKTP = owner?.pathKTP?.toString(),
            createdAt = owner?.createdAt.orEmpty(),
            isValidated = owner?.isValidated ?: false,
            phone = owner?.phone?.toString(),
            merchantId = owner?.merchantId.orEmpty(),
            couponUsed = owner?.couponUsed?.filterNotNull()?.map { it.toString() } ?: emptyList(),
            citizenNumber = owner?.citizenNumber?.toString(),
            name = owner?.name.orEmpty(),
            memberType = owner?.memberType?.toString(),
            id = owner?.id.orEmpty(),
            subscriptionStartDate = owner?.subscriptionStartDate?.toString(),
            email = owner?.email.orEmpty(),
            androidId = owner?.androidId?.toString(),
            refferalPoint = owner?.refferalPoint ?: 0
        )
    }

    private fun mapReceptionistToDomain(receptionist: Receptionist?): ReceptionistDomain {
        return ReceptionistDomain(
            role = receptionist?.role.orEmpty(),
            isPhoneVerified = receptionist?.isPhoneVerified ?: false,
            address = receptionist?.address?.toString(),
            subscriptionEndDate = receptionist?.subscriptionEndDate?.toString(),
            isNumberVerified = receptionist?.isNumberVerified ?: false,
            isEmailVerified = receptionist?.isEmailVerified ?: false,
            point = receptionist?.point ?: 0,
            pathKTP = receptionist?.pathKTP?.toString(),
            createdAt = receptionist?.createdAt.orEmpty(),
            isValidated = receptionist?.isValidated ?: false,
            phone = receptionist?.phone?.toString(),
            merchantId = receptionist?.merchantId.orEmpty(),
            couponUsed = receptionist?.couponUsed?.filterNotNull()?.map { it.toString() } ?: emptyList(),
            citizenNumber = receptionist?.citizenNumber?.toString(),
            name = receptionist?.name.orEmpty(),
            memberType = receptionist?.memberType?.toString(),
            id = receptionist?.id.orEmpty(),
            subscriptionStartDate = receptionist?.subscriptionStartDate?.toString(),
            email = receptionist?.email.orEmpty(),
            androidId = receptionist?.androidId?.toString(),
            refferalPoint = receptionist?.refferalPoint ?: 0
        )
    }

    private fun mapMerchantToDomain(merchant: Merchant?): MerchantDomain {
        return MerchantDomain(
            createdAt = merchant?.createdAt.orEmpty(),
            name = merchant?.name.orEmpty(),
            picturesUrl = merchant?.picturesUrl?.filterNotNull()?.map { it.toString() } ?: emptyList(),
            detail = merchant?.detail.orEmpty(),
            id = merchant?.id.orEmpty(),
            userId = merchant?.userId?.filterNotNull() ?: emptyList(),
            merchantType = merchant?.merchantType.orEmpty(),
            point = merchant?.point ?: 0,
            refferalPoint = merchant?.refferalPoint ?: 0
        )
    }

    fun mapGetMerchantsResponseToDomain(response: GetMerchantsResponse): GetMerchantsDomain {
        return GetMerchantsDomain(
            totalResults = response.totalResults ?: 0,
            limit = response.limit ?: 10,
            totalPages = response.totalPages ?: 0,
            page = response.page ?: 1,
            results = response.results?.filterNotNull()?.map { result ->
                MerchantResultDomain(
                    createdAt = result.createdAt.orEmpty(),
                    name = result.name.orEmpty(),
                    picturesUrl = result.picturesUrl?.filterNotNull()?.map { it.toString() } ?: emptyList(),
                    detail = result.detail.orEmpty(),
                    id = result.id.orEmpty(),
                    userId = result.userId?.filterNotNull() ?: emptyList(),
                    merchantType = result.merchantType.orEmpty(),
                    point = result.point ?: 0,
                    refferalPoint = result.refferalPoint ?: 0
                )
            } ?: emptyList()
        )
    }

    fun mapGetMerchantByIdResponseToDomain(response: GetMerchantsByIdResponse): GetMerchantByIdDomain {
        return GetMerchantByIdDomain(
            createdAt = response.createdAt.orEmpty(),
            name = response.name.orEmpty(),
            picturesUrl = response.picturesUrl?.filterNotNull() ?: emptyList(),
            detail = response.detail.orEmpty(),
            id = response.id.orEmpty(),
            userId = response.userId?.filterNotNull() ?: emptyList(),
            merchantType = response.merchantType.orEmpty(),
            point = response.point ?: 0,
            refferalPoint = response.refferalPoint ?: 0
        )
    }

    fun mapUpdateMerchantResponseToDomain(response: UpdateMerchantResponse): UpdateMerchantDomain {
        return UpdateMerchantDomain(
            createdAt = response.createdAt.orEmpty(),
            name = response.name.orEmpty(),
            phone = response.phone.orEmpty(),
            picturesUrl = response.picturesUrl?.filterNotNull()?.map { it.toString() } ?: emptyList(),
            detail = response.detail.orEmpty(),
            id = response.id.orEmpty(),
            userId = response.userId?.filterNotNull() ?: emptyList(),
            merchantType = response.merchantType.orEmpty(),
            point = response.point ?: 0,
            refferalPoint = response.refferalPoint ?: 0
        )
    }
}