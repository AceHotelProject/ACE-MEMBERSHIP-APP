package com.dicoding.core.data.source.remote.response.points

import com.google.gson.annotations.SerializedName

data class PointsResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("point_recieved")
    val pointReceived: Int? = null,

    @SerializedName("point_sent")
    val pointSent: Int? = null,

    @SerializedName("refferal_profit")
    val refferalProfit: Int? = null
)

data class PointHistoryResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("user_sender_id") val userSenderId: String? = null,
    @SerializedName("user_reciever_id") val userReceiverId: String? = null,
    @SerializedName("ammount") val amount: Int? = null,
    @SerializedName("note") val note: String? = null,
    @SerializedName("purpose") val purpose: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null
)

data class PointHistoryListResponse(
    val data: List<PointHistoryResponse> = emptyList()
)