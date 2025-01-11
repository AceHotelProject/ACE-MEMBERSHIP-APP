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
