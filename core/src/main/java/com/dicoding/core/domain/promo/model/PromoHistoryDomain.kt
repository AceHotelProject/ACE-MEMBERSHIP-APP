package com.dicoding.core.domain.promo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PromoHistoryDomain(
    val promoName: String,
    val promoCategory: String,
    val promoDetail: String,
    val promoPictures: List<String>,
    val promoTnc: List<String>,
    val promoMemberType: String,
    val userName: String,
    val tokenCode: String,
    val merchantName: String,
    val activationDate: String,
    val status: String,
    val id: String
): Parcelable