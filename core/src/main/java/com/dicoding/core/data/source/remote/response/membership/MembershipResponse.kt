package com.dicoding.core.data.source.remote.response.membership

import com.google.gson.annotations.SerializedName

data class MembershipResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("periode")
    val periode: Int? = null,

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("tnc")
    val tnc: List<String>? = null,

    @SerializedName("discount")
    val discount: Int? = null
)

data class MembershipListResponse(
    val data: List<MembershipResponse> = emptyList()
)