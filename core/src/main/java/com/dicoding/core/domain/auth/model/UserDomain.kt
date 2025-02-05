package com.dicoding.core.domain.auth.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
    val merchantId: String?,
    val couponUsed: List<String>,
    val point: Int,
    val refferalPoint: Int,
    val isNumberVerified: Boolean,
    val createdAt: String
) : Parcelable