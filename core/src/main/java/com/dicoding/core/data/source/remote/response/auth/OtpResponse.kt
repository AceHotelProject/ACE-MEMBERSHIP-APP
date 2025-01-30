package com.dicoding.core.data.source.remote.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OtpResponse(

	@field:SerializedName("expires")
	val expires: String? = null,

	@field:SerializedName("blacklisted")
	val blacklisted: Boolean? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("user")
	val user: String? = null,

	@field:SerializedName("token")
	val token: String? = null
) : Parcelable
