package com.dicoding.core.domain.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Merchant(
    val point: Int,
    val refferalPoint: Int,
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
    val memberType: String?,
    val couponUsed: List<String>,
    val point: Int,
    val refferalPoint: Int,
    val subscriptionStartDate: String?,
    val subscriptionEndDate: String?,
    val isEmailVerified: Boolean,
    val isNumberVerified: Boolean,
    val isValidated: Boolean
) : Parcelable

data class UserList(
    val data: List<User>,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
    val totalResults: Int
)