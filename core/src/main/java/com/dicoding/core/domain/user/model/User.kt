package com.dicoding.core.domain.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Merchant(
    val point: Int,
    val refferalPoint: Int,
    val id: String
) : Parcelable

@Parcelize
data class SubscriptionType(
    val type: String,
    val id: String
) : Parcelable

@Parcelize
data class Membership(
    val userId: String,
    val verificatorId: String?,
    val subscriptionType: SubscriptionType,
    val status: String,
    val payment: Int,
    val paymentProof: String?,
    val startDate: String,
    val endDate: String,
    val createdAt: String,
    val id: String
) : Parcelable

@Parcelize
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val citizenNumber: String?,
    val pathKTP: String?,
    val role: String,
    val merchantId: Merchant?,
    val androidId: String?,
    val couponUsed: List<String>,
    val point: Int,
    val refferalPoint: Int,
    val referralPoint: Int,
    val isEmailVerified: Boolean,
    val isNumberVerified: Boolean,
    val isPhoneVerified: Boolean,
    val isValidated: Boolean,
    val uniqueCode: String?,
    val referralToken: String?,
    val membership: Membership?,
    val isMember: Boolean,
    val createdAt: String?
) : Parcelable

data class UserList(
    val data: List<User>,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
    val totalResults: Int
)

@Parcelize
data class ReferralToken(
    val token: String,
    val userId: String,
    val type: String,
    val expires: Date?,
    val isBlacklisted: Boolean,
    val createdAt: Date,
    val id: String
) : Parcelable