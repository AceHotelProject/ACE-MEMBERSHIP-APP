package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.remote.response.user.MerchantIdResponse
import com.dicoding.core.data.source.remote.response.user.UserListResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import com.dicoding.core.domain.user.model.Merchant
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.model.UserList

object UserDataMapper {
    fun mapResponsesToDomain(response: UserListResponse): UserList = UserList(
        data = response.data.map { mapUserToDomain(it) },
        page = response.page,
        limit = response.limit,
        totalPages = response.totalPages,
        totalResults = response.totalResults
    )

    fun mapUserToDomain(input: UserResponse): User = User(
        id = input.id ?: "",
        name = input.name ?: "",
        email = input.email ?: "",
        phone = input.phone,
        address = input.address,
        citizenNumber = input.citizenNumber,
        pathKTP = input.pathKTP,
        role = input.role,
        merchantId = when (val merchantResponse = input.merchantId) {
            is MerchantIdResponse.MerchantData -> Merchant(
                point = merchantResponse.point,
                refferalPoint = merchantResponse.refferalPoint,
                id = merchantResponse.id
            )
            is MerchantIdResponse.MerchantString -> Merchant(
                point = 0,
                refferalPoint = 0,
                id = merchantResponse.id
            )
            null -> null
        },
        androidId = input.androidId,
        memberType = input.memberType,
        couponUsed = input.couponUsed,
        point = input.point,
        refferalPoint = input.refferalPoint,
        subscriptionStartDate = input.subscriptionStartDate,
        subscriptionEndDate = input.subscriptionEndDate,
        isEmailVerified = input.isEmailVerified,
        isNumberVerified = input.isNumberVerified,
        isValidated = input.isValidated
    )
}