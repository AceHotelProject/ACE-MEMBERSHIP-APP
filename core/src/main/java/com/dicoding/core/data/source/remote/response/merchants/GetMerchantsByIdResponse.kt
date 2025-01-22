package com.dicoding.core.data.source.remote.response.merchants

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetMerchantsByIdResponse(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("picturesUrl")
	val picturesUrl: List<String?>? = null,

	@field:SerializedName("detail")
	val detail: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userId")
	val userId: List<String?>? = null,

	@field:SerializedName("merchantType")
	val merchantType: String? = null,

	@field:SerializedName("point")
	val point: Int? = null,

	@field:SerializedName("refferalPoint")
	val refferalPoint: Int? = null
) : Parcelable
