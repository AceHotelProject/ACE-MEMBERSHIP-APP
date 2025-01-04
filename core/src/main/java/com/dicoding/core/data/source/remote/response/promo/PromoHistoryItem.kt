package com.dicoding.core.data.source.remote.response.promo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PromoHistoryItem(
    @field:SerializedName("promo_id")
    val promoId: String? = null,

    @field:SerializedName("redeem_date")
    val redeemDate: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("promo_name")
    val promoName: String? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable