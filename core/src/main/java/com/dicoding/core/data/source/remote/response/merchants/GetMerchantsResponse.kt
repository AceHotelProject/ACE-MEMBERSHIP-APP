package com.dicoding.core.data.source.remote.response.merchants

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetMerchantsResponse(
	val totalResults: Int? = null,
	val limit: Int? = null,
	val totalPages: Int? = null,
	val page: Int? = null,
	val results: List<ResultsItem?>? = null
) : Parcelable

@Parcelize
data class ResultsItem(
	val createdAt: String? = null,
	val name: String? = null,
	val picturesUrl: List<String?>? = null,
	val detail: String? = null,
	val id: String? = null,
	val userId: List<UserIdItem?>? = null,
	val merchantType: String? = null,
	val point: Int? = null,
	val refferalPoint: Int? = null
) : Parcelable

@Parcelize
data class UserIdItem(
	val phone: String? = null,
	val name: String? = null,
	val id: String? = null,
	val email: String? = null
) : Parcelable
