package com.dicoding.core.data.source.remote.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
	@field:SerializedName("tokens")
	val tokens: Tokens? = null,
	@field:SerializedName("user")
	val user: LoginUser? = null
) : Parcelable

@Parcelize
data class LoginUser(
	@field:SerializedName("role")
	val role: String? = null,
	@field:SerializedName("name")
	val name: String? = null,
	@field:SerializedName("id")
	val id: String? = null,
	@field:SerializedName("email")
	val email: String? = null,
	@field:SerializedName("isEmailVerified")
	val isEmailVerified: Boolean? = null
) : Parcelable

@Parcelize
data class Tokens(
	@field:SerializedName("access")
	val access: Access? = null,
	@field:SerializedName("refresh")
	val refresh: Refresh? = null
) : Parcelable

@Parcelize
data class Refresh(
	@field:SerializedName("expires")
	val expires: String? = null,
	@field:SerializedName("token")
	val token: String? = null
) : Parcelable

@Parcelize
data class Access(
	@field:SerializedName("expires")
	val expires: String? = null,
	@field:SerializedName("token")
	val token: String? = null
) : Parcelable
