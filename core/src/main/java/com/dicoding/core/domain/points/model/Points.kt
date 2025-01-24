package com.dicoding.core.domain.points.model

data class Points(
    val id: String,
    val points: Int,
    val totalPointIn: Int,
    val totalPointOut: Int,
    val totalPointReferral: Int
)
data class PointHistory(
    val type: String,
    val from: String,
    val to: String,
    val amount: Int,
    val notes: String,
    val createdAt: String,
    val id: String
)

data class UserPointHistory(
    val pointIn: Int,
    val pointOut: Int,
    val pointReferral: Int,
    val totalPoint: Int,
    val history: List<PointHistory>
)