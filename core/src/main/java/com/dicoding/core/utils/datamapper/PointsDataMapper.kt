package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.remote.response.points.PointHistoryResponse
import com.dicoding.core.data.source.remote.response.points.PointsResponse
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