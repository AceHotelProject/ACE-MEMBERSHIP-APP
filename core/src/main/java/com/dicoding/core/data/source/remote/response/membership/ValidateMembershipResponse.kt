package com.dicoding.core.data.source.remote.response.membership

import com.google.gson.annotations.SerializedName

data class ValidateMembershipResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("proof_image_path") val proofImagePath: String? = null,
    @SerializedName("verified_by") val verifiedBy: String? = null
)

data class ValidatedMembership(
    val id: String,
    val userId: String,
    val type: String,
    val price: Int,
    val startDate: String,
    val endDate: String,
    val status: String,
    val proofImagePath: String,
    val verifiedBy: String
)