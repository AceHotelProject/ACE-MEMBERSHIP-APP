package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.local.entity.auth.TokenEntity
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponseItem
import com.dicoding.core.data.source.remote.response.points.PointsResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import com.dicoding.core.domain.auth.model.TokensDomain
import com.dicoding.core.domain.auth.model.TokensFormat
import com.dicoding.core.domain.auth.model.UserDomain
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.model.PointsHistoryData
import com.dicoding.core.domain.points.model.UserPointHistory
import com.dicoding.core.domain.user.model.User

object PointsDataMapper {

    fun mapPointsResponseToDomain(input: PointsResponse): Points = Points(
        id = input.id ?: "",
        points = input.points,
        totalPointIn = input.totalPointIn,
        totalPointOut = input.totalPointOut,
        totalPointReferral = input.totalPointReferral
    )

    fun mapPointTransferResponseToDomain(input: PointHistoryResponseItem): PointHistory = PointHistory(
        type = input.type,
        from = PointsHistoryData(input.from.name!!, input.from.id!!),
        to = PointsHistoryData(input.to.name!!, input.to.id!!),
        amount = input.amount,
        notes = input.notes,
        createdAt = input.createdAt,
        id = input.id
    )

    fun mapUserPointsHistoryResponseToDomain(input: PointHistoryResponse): UserPointHistory = UserPointHistory(
        pointIn = input.pointIn,
        pointOut = input.pointOut,
        pointReferral = input.pointReferral,
        totalPoint = input.totalPoint,
        history = input.listHistory.map { mapPointTransferResponseToDomain(it) }
    )
}