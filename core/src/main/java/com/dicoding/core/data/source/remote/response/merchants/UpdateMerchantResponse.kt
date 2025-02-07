package com.dicoding.core.data.source.remote.response.merchants

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateMerchantResponse(
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

