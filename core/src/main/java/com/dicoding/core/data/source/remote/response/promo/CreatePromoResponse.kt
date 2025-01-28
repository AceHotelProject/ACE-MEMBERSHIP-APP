package com.dicoding.core.data.source.remote.response.promo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatePromoResponse(

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("member_type")
	val memberType: String? = null,

	@field:SerializedName("is_active")
	val isActive: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("tnc")
	val tnc: List<String?>? = null,

	@field:SerializedName("merchant_id")
	val merchantId: String? = null,

	@field:SerializedName("used")
	val used: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null,

	@field:SerializedName("pictures")
	val pictures: List<String?>? = null,

	@field:SerializedName("token_id")
	val tokenId: String? = null,

	@field:SerializedName("maximal_use")
	val maximalUse: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: String? = null,

	@field:SerializedName("detail")
	val detail: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null
) : Parcelable

data class CreatePromoRequest(
	val name: String?,
	val category: String?,
	val detail: String?,
	val pictures: List<String>?,
	val tnc: List<String>?,
	val start_date: String?,
	val end_date: String?,
	val member_type: String?,
	val maximal_use: Int?,
	val is_active: Boolean?
)