package com.dicoding.core.data.source.remote.response.merchants

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetMerchantStatistic(

	@field:SerializedName("totalPoint")
	val totalPoint: Int? = null,

	@field:SerializedName("pointOut")
	val pointOut: Int? = null,

	@field:SerializedName("totalPromoUsed")
	val totalPromoUsed: Int? = null,

	@field:SerializedName("pointIn")
	val pointIn: Int? = null,

	@field:SerializedName("totalPromo")
	val totalPromo: Int? = null,

	@field:SerializedName("pointReferral")
	val pointReferral: Int? = null
) : Parcelable
