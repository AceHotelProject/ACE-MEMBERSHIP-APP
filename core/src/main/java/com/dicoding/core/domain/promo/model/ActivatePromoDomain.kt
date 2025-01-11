package com.dicoding.core.domain.promo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivatePromoDomain(
    val expires: String,
    val blacklisted: Boolean,
    val id: String,
    val type: String,
    val user: String,
    val token: String
) : Parcelable