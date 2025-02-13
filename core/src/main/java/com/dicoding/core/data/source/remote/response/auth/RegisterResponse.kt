package com.dicoding.core.data.source.remote.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterResponse(
	val tokens: Tokens? = null,
	val user: AuthUser? = null
) : Parcelable

@Parcelize
data class AuthUser(
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("isValidated")
	val isValidated: Boolean? = null,

	@field:SerializedName("isEmailVerified")
	val isEmailVerified: Boolean? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("isPhoneVerified")
	val isPhoneVerified: Boolean? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("citizenNumber")
	val citizenNumber: String? = null,

	@field:SerializedName("pathKTP")
	val pathKTP: String? = null,

	@field:SerializedName("androidId")
	val androidId: String? = null,

	@field:SerializedName("merchantId")
	val merchantId: String? = null,

	@field:SerializedName("couponUsed")
	val couponUsed: List<String>? = emptyList(),

	@field:SerializedName("point")
	val point: Int? = 0,

	@field:SerializedName("refferalPoint")
	val refferalPoint: Int? = 0,

	@field:SerializedName("isNumberVerified")
	val isNumberVerified: Boolean? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isMember")
	val isMember: Boolean? = null
) : Parcelable
