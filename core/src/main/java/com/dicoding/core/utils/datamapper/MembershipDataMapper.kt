package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.local.entity.auth.TokenEntity
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import com.dicoding.core.domain.auth.model.TokensDomain
import com.dicoding.core.domain.auth.model.TokensFormat
import com.dicoding.core.domain.auth.model.UserDomain
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.user.model.User

object MembershipDataMapper {
    fun mapResponseToDomain(input: MembershipResponse): Membership = Membership(
        id = input.id ?: "",
        type = input.type ?: "",
        duration = input.duration ?: 0,
        price = input.price ?: 0,
        tnc = input.tnc ?: emptyList()
    )

}