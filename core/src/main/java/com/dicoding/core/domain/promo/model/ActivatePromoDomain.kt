package com.dicoding.core.domain.promo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivatePromoDomain(
    val promoPictures: List<String>,
    val activationDate: String,
    val promoDetail: String,
    val userName: String,
    val promoName: String,
    val promoCategory: String,
    val merchantName: String,
    val merchant: String,
    val promoTnc: List<String>,
    val token: String,
    val promo: String,
    val tokenCode: String,
    val id: String,
    val promoMemberType: String,
    val user: String,
    val status: String
) : Parcelable