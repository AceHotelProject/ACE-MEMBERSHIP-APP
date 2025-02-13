package com.dicoding.core.domain.points.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Points(
    val id: String,
    val points: Int,
    val totalPointIn: Int,
    val totalPointOut: Int,
    val totalPointReferral: Int
)

@Parcelize
data class PointsHistoryData(
    val name: String,
    val email: String,
    val phone: String,
    val id: String
): Parcelable

@Parcelize
data class PointHistory(
    val type: String,
    val from: PointsHistoryData,
    val to: PointsHistoryData,
    val amount: Int,
    val notes: String,
    val createdAt: String,
    val id: String
) : Parcelable

data class UserPointHistory(
    val pointIn: Int,
    val pointOut: Int,
    val pointReferral: Int,
    val totalPoint: Int,
    val history: List<PointHistory>
)