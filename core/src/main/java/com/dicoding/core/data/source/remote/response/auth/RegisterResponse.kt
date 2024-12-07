package com.dicoding.core.data.source.remote.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterResponse(
	val tokens: Tokens? = null,
	val user: RegisterUser? = null
) : Parcelable

@Parcelize
data class RegisterUser(
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
