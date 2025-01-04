package com.dicoding.core.domain.promo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeletePromoDomain(
    val success: Boolean,
    val message: String,
    val id: String
) : Parcelable