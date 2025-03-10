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

    @SerializedName("image")
    val image: List<String>? = null,

    @SerializedName("maxCoupon")
    val maxCoupon: Int? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null
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

data class SubscriptionHistoryResponse(
    @SerializedName("results")
    val results: List<SubscriptionItem> = emptyList(),

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("limit")
    val limit: Int = 10,

    @SerializedName("totalPages")
    val totalPages: Int = 0,

    @SerializedName("totalResults")
    val totalResults: Int = 0
)

data class SubscriptionItem(
    @SerializedName("userId")
    val userId: UserInfo? = null,

    @SerializedName("verificatorId")
    val verificatorId: UserInfo? = null,

    @SerializedName("subscriptionType")
    val subscriptionType: SubscriptionType? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("payment")
    val payment: Int? = null,

    @SerializedName("paymentProof")
    val paymentProof: String? = null,

    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("endDate")
    val endDate: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("id")
    val id: String? = null
)

data class UserInfo(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("isMember")
    val isMember: Boolean = false,

    @SerializedName("id")
    val id: String? = null
)

data class SubscriptionType(
    @SerializedName("type")
    val type: String? = null,

    @SerializedName("id")
    val id: String? = null
)