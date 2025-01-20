package com.dicoding.core.data.source.remote.response.membership

import com.google.gson.annotations.SerializedName

data class MembershipResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("tnc")
    val tnc: List<String>? = null,
    )

data class MembershipListResponse(
    @SerializedName("results")
    val results: List<MembershipResponse> = emptyList(),

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("limit")
    val limit: Int = 10,

    @SerializedName("totalPages")
    val totalPages: Int = 0,

    @SerializedName("totalResults")
    val totalResults: Int = 0
)