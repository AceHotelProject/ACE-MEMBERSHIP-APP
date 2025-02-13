package com.dicoding.core.utils.datamapper

import android.util.Log
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponseItem
import com.dicoding.core.data.source.remote.response.points.PointsResponse
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.model.PointsHistoryData
import com.dicoding.core.domain.points.model.UserPointHistory

object PointsDataMapper {
    private const val TAG = "PointsDataMapper"

    fun mapPointsResponseToDomain(input: PointsResponse): Points = Points(
        id = input.id ?: "",
        points = input.points,
        totalPointIn = input.totalPointIn,
        totalPointOut = input.totalPointOut,
        totalPointReferral = input.totalPointReferral
    )

    fun mapPointTransferResponseToDomain(input: PointHistoryResponseItem): PointHistory {
        Log.d(TAG, "Processing history item - Type: ${input.type}, Amount: ${input.amount}")

        // Log sender details with null check
        Log.d(TAG, "Sender details - Name: ${input.from.name}, Email: ${input.from.email}, Phone: ${input.from.phone}, ID: ${input.from.id}")

        // Log recipient details with null check
        Log.d(TAG, "Recipient details - Name: ${input.to.name}, Email: ${input.to.email}, Phone: ${input.to.phone}, ID: ${input.to.id}")

        return PointHistory(
            type = input.type,
            from = PointsHistoryData(
                name = input.from.name ?: "empty",
                email = input.from.email ?: "empty",
                phone = input.from.phone ?: "empty",
                id = input.from.id ?: "empty"
            ),
            to = PointsHistoryData(
                name = input.to.name ?: "empty",
                email = input.to.email ?: "empty",
                phone = input.to.phone ?: "empty",
                id = input.to.id ?: "empty"
            ),
            amount = input.amount,
            notes = input.notes,
            createdAt = input.createdAt,
            id = input.id
        ).also {
            Log.d(TAG, "Successfully mapped history item with ID: ${it.id}")
        }
    }

    fun mapUserPointsHistoryResponseToDomain(input: PointHistoryResponse): UserPointHistory {
        Log.d(TAG, "Starting to map history with ${input.listHistory.size} items")

        val mappedHistory = input.listHistory.mapIndexed { index, item ->
            try {
                Log.d(TAG, "Processing history item ${index + 1}/${input.listHistory.size}")
                mapPointTransferResponseToDomain(item)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to map history item $index - Error: ${e.message}", e)
                throw e
            }
        }

        Log.d(TAG, "Successfully mapped ${mappedHistory.size} history items")

        return UserPointHistory(
            pointIn = input.pointIn,
            pointOut = input.pointOut,
            pointReferral = input.pointReferral,
            totalPoint = input.totalPoint,
            history = mappedHistory
        )
    }

}