package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.local.entity.auth.TokenEntity
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.data.source.remote.response.membership.ValidateMembershipResponse
import com.dicoding.core.data.source.remote.response.membership.ValidatedMembership
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
        name = input.name ?: "",
        periode = input.periode ?: 0,
        price = input.price ?: 0,
        tnc = input.tnc ?: emptyList(),
        discount = input.discount ?: 0
    )

    fun mapValidateResponseToDomain(input: ValidateMembershipResponse): ValidatedMembership = ValidatedMembership(
        id = input.id ?: "",
        userId = input.userId ?: "",
        type = input.type ?: "",
        price = input.price ?: 0,
        startDate = input.startDate ?: "",
        endDate = input.endDate ?: "",
        status = input.status ?: "",
        proofImagePath = input.proofImagePath ?: "",
        verifiedBy = input.verifiedBy ?: ""
    )
}