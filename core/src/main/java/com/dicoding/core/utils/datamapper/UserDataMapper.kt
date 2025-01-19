package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.local.entity.auth.TokenEntity
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import com.dicoding.core.domain.auth.model.TokensDomain
import com.dicoding.core.domain.auth.model.TokensFormat
import com.dicoding.core.domain.auth.model.UserDomain
import com.dicoding.core.domain.user.model.User

object UserDataMapper {

    fun mapUserResponseToDomain(input: UserResponse): User = User(
        id = input.id ?: "Empty",
        name = input.name ?: "Empty",
        email = input.email ?: "Empty",
        phone = input.phone ?: "Empty",
        address = input.address ?: "Empty",
        citizenNumber = input.citizenNumber ?: "Empty",
        idPicturePath = input.idPicturePath ?: "Empty",
        role = input.role ?: "nonMember",  // Added role with default
        merchant_id = input.merchantId ?: "Empty",
        android_id = input.androidId ?: "Empty",
        coupon_used = input.couponUsed ?: 0,
        point = input.point ?: 0,
        subscription_start_date = input.subscriptionStartDate ?: "Empty",
        subscription_end_date = input.subscriptionEndDate ?: "Empty"
    )
}