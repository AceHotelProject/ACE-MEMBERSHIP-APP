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
import com.dicoding.core.domain.merchants.model.MerchantUser
import com.dicoding.core.domain.merchants.model.OwnerDomain
import com.dicoding.core.domain.merchants.model.ReceptionistDomain
import com.dicoding.core.domain.merchants.model.UpdateMerchantDomain
import com.dicoding.core.domain.merchants.model.UserIdDomain

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
            address = owner?.address,
            subscriptionEndDate = owner?.subscriptionEndDate,
            isNumberVerified = owner?.isNumberVerified ?: false,
            isEmailVerified = owner?.isEmailVerified ?: false,
            point = owner?.point ?: 0,
            pathKTP = owner?.pathKTP,
            createdAt = owner?.createdAt.orEmpty(),
            isValidated = owner?.isValidated ?: false,
            phone = owner?.phone,
            merchantId = owner?.merchantId.orEmpty(),
            couponUsed = owner?.couponUsed?.map { it } ?: emptyList(),
            citizenNumber = owner?.citizenNumber,
            name = owner?.name.orEmpty(),
            memberType = owner?.memberType,
            id = owner?.id.orEmpty(),
            subscriptionStartDate = owner?.subscriptionStartDate,
            email = owner?.email.orEmpty(),
            androidId = owner?.androidId,
            refferalPoint = owner?.refferalPoint ?: 0
        )
    }

    private fun mapReceptionistToDomain(receptionist: Receptionist?): ReceptionistDomain {
        return ReceptionistDomain(
            role = receptionist?.role.orEmpty(),
            isPhoneVerified = receptionist?.isPhoneVerified ?: false,
            address = receptionist?.address,
            subscriptionEndDate = receptionist?.subscriptionEndDate,
            isNumberVerified = receptionist?.isNumberVerified ?: false,
            isEmailVerified = receptionist?.isEmailVerified ?: false,
            point = receptionist?.point ?: 0,
            pathKTP = receptionist?.pathKTP,
            createdAt = receptionist?.createdAt.orEmpty(),
            isValidated = receptionist?.isValidated ?: false,
            phone = receptionist?.phone,
            merchantId = receptionist?.merchantId.orEmpty(),
            couponUsed = receptionist?.couponUsed?.map { it } ?: emptyList(),
            citizenNumber = receptionist?.citizenNumber,
            name = receptionist?.name.orEmpty(),
            memberType = receptionist?.memberType,
            id = receptionist?.id.orEmpty(),
            subscriptionStartDate = receptionist?.subscriptionStartDate,
            email = receptionist?.email.orEmpty(),
            androidId = receptionist?.androidId,
            refferalPoint = receptionist?.refferalPoint ?: 0
        )
    }

    private fun mapMerchantToDomain(merchant: Merchant?): MerchantDomain {
        return MerchantDomain(
            createdAt = merchant?.createdAt.orEmpty(),
            name = merchant?.name.orEmpty(),
            picturesUrl = merchant?.picturesUrl?.map { it } ?: emptyList(),
            detail = merchant?.detail.orEmpty(),
            id = merchant?.id.orEmpty(),
            userId = merchant?.userId ?: emptyList(),
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
                    picturesUrl = result.picturesUrl?.filterNotNull()?.map { it } ?: emptyList(),
                    detail = result.detail.orEmpty(),
                    id = result.id.orEmpty(),
                    userId = result.userId?.filterNotNull()?.map { userId ->
                        UserIdDomain(
                            phone = userId.phone.orEmpty(),
                            name = userId.name.orEmpty(),
                            id = userId.id.orEmpty(),
                            email = userId.email.orEmpty()
                        )
                    } ?: emptyList(),
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
            userId = response.userId?.filterNotNull()?.map { user ->
                MerchantUser(
                    name = user.name.orEmpty(),
                    email = user.email.orEmpty(),
                    phone = user.phone.orEmpty(),
                    id = user.id.orEmpty()
                )
            } ?: emptyList(),
            merchantType = response.merchantType.orEmpty(),
            point = response.point ?: 0,
            refferalPoint = response.refferalPoint ?: 0
        )
    }

    fun mapUpdateMerchantResponseToDomain(response: UpdateMerchantResponse): UpdateMerchantDomain {
        return UpdateMerchantDomain(
            createdAt = response.createdAt.orEmpty(),
            name = response.name.orEmpty(),
            picturesUrl = response.picturesUrl?.filterNotNull()?.map { it } ?: emptyList(),
            detail = response.detail.orEmpty(),
            id = response.id.orEmpty(),
            userId = response.userId?.filterNotNull()?.map { userIdItem ->
                MerchantUser(
                    phone = userIdItem.phone.orEmpty(),
                    name = userIdItem.name.orEmpty(),
                    id = userIdItem.id.orEmpty(),
                    email = userIdItem.email.orEmpty()
                )
            } ?: emptyList(),
            merchantType = response.merchantType.orEmpty(),
            point = response.point ?: 0,
            refferalPoint = response.refferalPoint ?: 0
        )
    }
}