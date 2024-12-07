package com.dicoding.core.domain.auth.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDomain(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isEmailVerified: Boolean
) : Parcelable