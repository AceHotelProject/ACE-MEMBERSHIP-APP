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
    val merchantId: String,
    val activatedByName: String,
    val activatedById: String,
    val activationDate: String,
    val redeemedByName: String?,
    val redeemedById: String?,
    val redeemedDate: String?,
    val draftDate: String,
    val draftedByName: String,
    val draftedById: String,
    val validatedByName: String,
    val validatedById: String,
    val validatedDate: String,
    val status: String,
    val id: String,
    val maximalUse: Int,
    val duration: Int,
    val expiredDate: String,
    val createdAt: String,
    val count: Int
): Parcelable