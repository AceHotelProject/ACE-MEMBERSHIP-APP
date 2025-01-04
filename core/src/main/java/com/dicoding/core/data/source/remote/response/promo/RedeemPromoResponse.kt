package com.dicoding.core.data.source.remote.response.promo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RedeemPromoResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("promo_id")
    val promoId: String? = null,

    @field:SerializedName("redeem_date")
    val redeemDate: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null
) : Parcelable