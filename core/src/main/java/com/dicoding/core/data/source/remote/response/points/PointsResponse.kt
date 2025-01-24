package com.dicoding.core.data.source.remote.response.points

import com.google.gson.annotations.SerializedName

data class PointsResponse(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("points")
    val points: Int = 0,

    @SerializedName("totalPointIn")
    val totalPointIn: Int = 0,

    @SerializedName("totalPointOut")
    val totalPointOut: Int = 0,

    @SerializedName("totalPointReferral")
    val totalPointReferral: Int = 0
)

data class PointsInfo(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("id")
    val id: String? = null

)

data class PointHistoryResponseItem(
    @SerializedName("type")
    val type: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("to")
    val to: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("notes")
    val notes: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: String
)

data class PointHistoryResponse(
    @SerializedName("pointIn")
    val pointIn: Int = 0,

    @SerializedName("pointOut")
    val pointOut: Int = 0,

    @SerializedName("pointReferral")
    val pointReferral: Int = 0,

    @SerializedName("totalPoint")
    val totalPoint: Int = 0,

    //perlu di revisi
    @SerializedName("history")
    val listHistory: List<PointHistoryResponseItem> = emptyList()
)