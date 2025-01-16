package com.dicoding.core.data.source.remote.response.user

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("name")
    val name: String? = "Anonymous",

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("is_validated")
    val isValidated: Boolean = false,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("citizen_number")
    val citizenNumber: String? = null,

    @field:SerializedName("id_picture_path")
    val idPicturePath: String? = null,

    @field:SerializedName("role")
    val role: String = "nonMember",

    @field:SerializedName("merchant_id")
    val merchantId: String? = null,

    @field:SerializedName("android_id")
    val androidId: String? = null,

    @field:SerializedName("member_type")
    val memberType: String? = null,

    @field:SerializedName("coupon_used")
    val couponUsed: Int? = null,

    @field:SerializedName("point")
    val point: Int = 0,

    @field:SerializedName("refferal_point")
    val refferalPoint: Int? = null,

    @field:SerializedName("subscription_start_date")
    val subscriptionStartDate: String? = null,

    @field:SerializedName("subscription_end_date")
    val subscriptionEndDate: String? = null
)

data class UserListResponse(
    val data: List<UserResponse> = emptyList()
)