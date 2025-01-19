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
    val data: List<MembershipResponse> = emptyList()
)