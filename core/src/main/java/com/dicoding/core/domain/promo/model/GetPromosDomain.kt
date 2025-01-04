package com.dicoding.core.domain.promo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPromosDomain(
    val totalResults: Int,
    val limit: Int,
    val totalPages: Int,
    val page: Int,
    val results: List<PromoDomain>
) : Parcelable