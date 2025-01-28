package com.dicoding.core.data.source.remote.response.merchants

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateMerchantResponse(
	@field:SerializedName("owner")
	val owner: Owner? = null,

	@field:SerializedName("receptionist")
	val receptionist: Receptionist? = null,

	@field:SerializedName("merchant")
	val merchant: Merchant? = null
) : Parcelable

@Parcelize
data class Owner(
	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("isPhoneVerified")
	val isPhoneVerified: Boolean? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("subscriptionEndDate")
	val subscriptionEndDate: String? = null,

	@field:SerializedName("isNumberVerified")
	val isNumberVerified: Boolean? = null,

	@field:SerializedName("isEmailVerified")
	val isEmailVerified: Boolean? = null,

	@field:SerializedName("point")
	val point: Int? = null,

	@field:SerializedName("pathKTP")
	val pathKTP: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isValidated")
	val isValidated: Boolean? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("merchantId")
	val merchantId: String? = null,

	@field:SerializedName("couponUsed")
	val couponUsed: List<String>? = null,

	@field:SerializedName("citizenNumber")
	val citizenNumber: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("memberType")
	val memberType: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("subscriptionStartDate")
	val subscriptionStartDate: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("androidId")
	val androidId: String? = null,

	@field:SerializedName("refferalPoint")
	val refferalPoint: Int? = null
) : Parcelable

@Parcelize
data class Receptionist(
	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("isPhoneVerified")
	val isPhoneVerified: Boolean? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("subscriptionEndDate")
	val subscriptionEndDate: String? = null,

	@field:SerializedName("isNumberVerified")
	val isNumberVerified: Boolean? = null,

	@field:SerializedName("isEmailVerified")
	val isEmailVerified: Boolean? = null,

	@field:SerializedName("point")
	val point: Int? = null,

	@field:SerializedName("pathKTP")
	val pathKTP: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isValidated")
	val isValidated: Boolean? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("merchantId")
	val merchantId: String? = null,

	@field:SerializedName("couponUsed")
	val couponUsed: List<String>? = null,

	@field:SerializedName("citizenNumber")
	val citizenNumber: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("memberType")
	val memberType: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("subscriptionStartDate")
	val subscriptionStartDate: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("androidId")
	val androidId: String? = null,

	@field:SerializedName("refferalPoint")
	val refferalPoint: Int? = null
) : Parcelable

@Parcelize
data class Merchant(
	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("picturesUrl")
	val picturesUrl: List<String>? = null,

	@field:SerializedName("detail")
	val detail: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userId")
	val userId: List<String>? = null,

	@field:SerializedName("merchantType")
	val merchantType: String? = null,

	@field:SerializedName("point")
	val point: Int? = null,

	@field:SerializedName("refferalPoint")
	val refferalPoint: Int? = null
) : Parcelable


@Parcelize
data class CreateMerchantRequest(
	@field:SerializedName("merchantData")
	val merchantData: MerchantData,

	@field:SerializedName("ownerData")
	val ownerData: UserData,

	@field:SerializedName("receptionistData")
	val receptionistData: UserData
): Parcelable

@Parcelize
data class MerchantData(
	@field:SerializedName("picturesUrl")
	val picturesUrl: List<String> = emptyList(),

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("merchantType")
	val merchantType: String,

	@field:SerializedName("detail")
	val detail: String,
) : Parcelable

@Parcelize
data class UserData(
	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("password")
	val password: String
):Parcelable
