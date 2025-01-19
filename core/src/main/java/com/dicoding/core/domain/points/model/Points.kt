package com.dicoding.core.domain.points.model

data class Points(
    val id: String,
    val pointReceived: Int,
    val pointSent: Int,
    val refferalProfit: Int
)

data class PointHistory(
    val id: String,
    val type: String,
    val userSenderId: String,
    val userReceiverId: String,
    val amount: Int,
    val note: String,
    val purpose: String,
    val timestamp: String
)