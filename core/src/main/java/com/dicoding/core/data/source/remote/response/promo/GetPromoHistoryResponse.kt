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

    @field:SerializedName("promo_member_type")
    val promoMemberType: String? = null,

    @field:SerializedName("user_name")
    val userName: String? = null,

    @field:SerializedName("token_code")
    val tokenCode: String? = null,

    @field:SerializedName("merchant_name")
    val merchantName: String? = null,

    @field:SerializedName("activation_date")
    val activationDate: String? = null,

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
    val id: String? = null
) : Parcelable
