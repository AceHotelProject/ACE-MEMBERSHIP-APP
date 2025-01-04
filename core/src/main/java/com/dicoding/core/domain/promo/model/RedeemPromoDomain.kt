package com.dicoding.core.domain.promo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RedeemPromoDomain(
    val success: Boolean,
    val message: String,
    val promoId: String,
    val redeemDate: String,
    val userId: String
) : Parcelable