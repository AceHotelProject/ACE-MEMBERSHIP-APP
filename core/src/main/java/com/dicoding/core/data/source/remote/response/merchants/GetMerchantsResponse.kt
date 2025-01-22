package com.dicoding.core.data.source.remote.response.merchants

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetMerchantsResponse(

	@field:SerializedName("totalResults")
	val totalResults: Int? = null,

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null,

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null
)

data class ResultsItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("picturesUrl")
	val picturesUrl: List<Any?>? = null,

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
)
