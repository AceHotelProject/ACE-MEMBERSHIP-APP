package com.dicoding.core.domain.promo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PromoDomain(
    val id: String,
    val name: String,
    val category: String,
    val detail: String,
    val pictures: List<String>,
    val tnc: List<String>,
    val startDate: String,
    val endDate: String,
    val memberType: String,
    val merchantId: String,
    val maximalUse: Int,
    val used: Int,
    val isActive: Boolean,
    val createdBy: String?,
    val updatedBy: String?,
    val token: String?
) : Parcelable