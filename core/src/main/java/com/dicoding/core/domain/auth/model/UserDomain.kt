package com.dicoding.core.domain.auth.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

// Domain models
@Parcelize
data class MerchantIdDomain(
    val id: String,
    val point: Int,
    val referralPoint: Int
) : Parcelable

@Parcelize
data class UserDomain(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isValidated: Boolean,
    val isEmailVerified: Boolean,
    val phone: String?,
    val isPhoneVerified: Boolean,
    val address: String?,
    val citizenNumber: String?,
    val pathKTP: String?,
    val androidId: String?,
    val merchantId: @RawValue MerchantIdDomain?,
    val couponUsed: List<String>,
    val point: Int,
    val refferalPoint: Int,
    val isNumberVerified: Boolean,
    val createdAt: String,
    val isMember: Boolean
) : Parcelable