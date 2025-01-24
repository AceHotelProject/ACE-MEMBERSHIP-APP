    package com.dicoding.core.data.source.remote.response.user

    import com.google.gson.annotations.SerializedName

    data class UserResponse(
        @SerializedName("id")
        val id: String? = null,

        @SerializedName("name")
        val name: String? = null,

        @SerializedName("email")
        val email: String? = null,

        @SerializedName("isEmailVerified")
        val isEmailVerified: Boolean = false,

        @SerializedName("isNumberVerified")
        val isNumberVerified: Boolean = false,

        @SerializedName("isPhoneVerified")
        val isPhoneVerified: Boolean = false,

        @SerializedName("isValidated")
        val isValidated: Boolean = false,

        @SerializedName("phone")
        val phone: String? = null,

        @SerializedName("address")
        val address: String? = null,

        @SerializedName("citizenNumber")
        val citizenNumber: String? = null,

        @SerializedName("pathKTP")
        val pathKTP: String? = null,

        @SerializedName("role")
        val role: String = "user",

        @SerializedName("merchantId")
        val merchantId: String? = null,

        @SerializedName("androidId")
        val androidId: String? = null,

        @SerializedName("memberType")
        val memberType: String? = null,

        @SerializedName("couponUsed")
        val couponUsed: List<String> = emptyList(),

        @SerializedName("point")
        val point: Int = 0,

        @SerializedName("refferalPoint")
        val refferalPoint: Int = 0,

        @SerializedName("subscriptionStartDate")
        val subscriptionStartDate: String? = null,

        @SerializedName("subscriptionEndDate")
        val subscriptionEndDate: String? = null,

        @SerializedName("createdAt")
        val createdAt: String? = null
    )

    data class UserListResponse(
        @SerializedName("results")
        val data: List<UserResponse> = emptyList(),

        @SerializedName("page")
        val page: Int = 1,

        @SerializedName("limit")
        val limit: Int = 10,

        @SerializedName("totalPages")
        val totalPages: Int = 1,

        @SerializedName("totalResults")
        val totalResults: Int = 0
    )