package com.dicoding.core.domain.merchants.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateMerchantDomain(
    val owner: OwnerDomain,
    val receptionist: ReceptionistDomain,
    val merchant: MerchantDomain
) : Parcelable

@Parcelize
data class OwnerDomain(
    val role: String,
    val isPhoneVerified: Boolean,
    val address: String?,
    val subscriptionEndDate: String?,
    val isNumberVerified: Boolean,
    val isEmailVerified: Boolean,
    val point: Int,
    val pathKTP: String?,
    val createdAt: String,
    val isValidated: Boolean,
    val phone: String?,
    val merchantId: String,
    val couponUsed: List<String>,
    val citizenNumber: String?,
    val name: String,
    val memberType: String?,
    val id: String,
    val subscriptionStartDate: String?,
    val email: String,
    val androidId: String?,
    val refferalPoint: Int
) : Parcelable

@Parcelize
data class ReceptionistDomain(
    val role: String,
    val isPhoneVerified: Boolean,
    val address: String?,
    val subscriptionEndDate: String?,
    val isNumberVerified: Boolean,
    val isEmailVerified: Boolean,
    val point: Int,
    val pathKTP: String?,
    val createdAt: String,
    val isValidated: Boolean,
    val phone: String?,
    val merchantId: String,
    val couponUsed: List<String>,
    val citizenNumber: String?,
    val name: String,
    val memberType: String?,
    val id: String,
    val subscriptionStartDate: String?,
    val email: String,
    val androidId: String?,
    val refferalPoint: Int
) : Parcelable

@Parcelize
data class MerchantDomain(
    val createdAt: String,
    val name: String,
    val picturesUrl: List<String>,
    val detail: String,
    val id: String,
    val userId: List<String>,
    val merchantType: String,
    val point: Int,
    val refferalPoint: Int
) : Parcelable

@Parcelize
data class GetMerchantsDomain(
    val totalResults: Int,
    val limit: Int,
    val totalPages: Int,
    val page: Int,
    val results: List<MerchantResultDomain>
) : Parcelable

@Parcelize
data class MerchantResultDomain(
    val createdAt: String,
    val name: String,
    val picturesUrl: List<String>,
    val detail: String,
    val id: String,
    val userId: List<String>,
    val merchantType: String,
    val point: Int,
    val refferalPoint: Int
) : Parcelable

@Parcelize
data class GetMerchantByIdDomain(
    val createdAt: String,
    val name: String,
    val picturesUrl: List<String>,
    val detail: String,
    val id: String,
    val userId: List<String>,
    val merchantType: String,
    val point: Int,
    val refferalPoint: Int
) : Parcelable

@Parcelize
data class UpdateMerchantDomain(
    val createdAt: String,
    val name: String,
    val picturesUrl: List<String>,
    val detail: String,
    val id: String,
    val userId: List<String>,
    val merchantType: String,
    val point: Int,
    val refferalPoint: Int
) : Parcelable