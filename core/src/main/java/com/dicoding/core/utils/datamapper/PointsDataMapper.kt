package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.local.entity.auth.TokenEntity
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.data.source.remote.response.membership.ValidateMembershipResponse
import com.dicoding.core.data.source.remote.response.membership.ValidatedMembership
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponse
import com.dicoding.core.data.source.remote.response.points.PointsResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import com.dicoding.core.domain.auth.model.TokensDomain
import com.dicoding.core.domain.auth.model.TokensFormat
import com.dicoding.core.domain.auth.model.UserDomain
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.user.model.User

object PointsDataMapper {
    fun mapResponseToDomain(input: PointsResponse): Points = Points(
        id = input.id ?: "",
        pointReceived = input.pointReceived ?: 0,
        pointSent = input.pointSent ?: 0,
        refferalProfit = input.refferalProfit ?: 0
    )

    fun mapHistoryResponseToDomain(input: PointHistoryResponse): PointHistory = PointHistory(
        id = input.id ?: "",
        type = input.type ?: "",
        userSenderId = input.userSenderId ?: "",
        userReceiverId = input.userReceiverId ?: "",
        amount = input.amount ?: 0,
        note = input.note ?: "",
        purpose = input.purpose ?: "",
        timestamp = input.timestamp ?: ""
    )
}