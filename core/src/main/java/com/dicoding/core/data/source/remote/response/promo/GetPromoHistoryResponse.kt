package com.dicoding.core.data.source.remote.response.promo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPromoHistoryResponse(
    @field:SerializedName("totalResults")
    val totalResults: Int? = null,

    @field:SerializedName("limit")
    val limit: Int? = null,

    @field:SerializedName("totalPages")
    val totalPages: Int? = null,

    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("results")
    val results: List<PromoHistoryItem?>? = null
) : Parcelable

@Parcelize
data class PromoHistoryItem(
    @field:SerializedName("promo_name")
    val promoName: String? = null,

    @field:SerializedName("promo_category")
    val promoCategory: String? = null,

    @field:SerializedName("promo_detail")
    val promoDetail: String? = null,

    @field:SerializedName("promo_pictures")
    val promoPictures: List<String>? = null,

    @field:SerializedName("promo_tnc")
    val promoTnc: List<String>? = null,

    @field:SerializedName("member_type")
    val promoMemberType: String? = null,

    @field:SerializedName("user_name")
    val userName: String? = null,

    @field:SerializedName("token_code")
    val tokenCode: String? = null,

    @field:SerializedName("merchant_name")
    val merchantName: String? = null,

    @field:SerializedName("merchant_id")
    val merchantId: String? = null,

    @field:SerializedName("activated_by_name")
    val activatedByName: String? = null,

    @field:SerializedName("activated_by_id")
    val activatedById: String? = null,

    @field:SerializedName("activation_date")
    val activationDate: String? = null,

    @field:SerializedName("redeemed_by_name")
    val redeemedByName: String? = null,

    @field:SerializedName("redeemed_by_id")
    val redeemedById: String? = null,

    @field:SerializedName("redeemed_date")
    val redeemedDate: String? = null,

    @field:SerializedName("draft_date")
    val draftDate: String? = null,

    @field:SerializedName("drafted_by_name")
    val draftedByName: String? = null,

    @field:SerializedName("drafted_by_id")
    val draftedById: String? = null,

    @field:SerializedName("validated_by_name")
    val validatedByName: String? = null,

    @field:SerializedName("validated_by_id")
    val validatedById: String? = null,

    @field:SerializedName("validated_date")
    val validatedDate: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("promo")
    val promo: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("merchant")
    val merchant: String? = null,

    @field:SerializedName("user")
    val user: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("maximal_use")
    val maximalUse: Int? = null,

    @field:SerializedName("duration")
    val duration: Int? = null,

    @field:SerializedName("expired_date")
    val expiredDate: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("count")
    val count: Int? = null
) : Parcelable
