package com.dicoding.core.domain.promo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPromoHistoryDomain(
    val totalResults: Int,
    val limit: Int,
    val totalPages: Int,
    val page: Int,
    val results: List<PromoHistoryDomain>
) : Parcelable

@Parcelize
data class PromoHistoryDomain(
    val promoId: String,
    val redeemDate: String,
    val userId: String,
    val promoName: String,
    val status: String
) : Parcelable