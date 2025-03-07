package com.dicoding.core.domain.membership.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

data class Membership(
    val id: String,
    val type: String,
    val maxCoupon: Int,
    val duration: Int,
    val price: Int,
    val tnc: List<String>,
    val image: List<String>,
    val createdAt: String
)

@Parcelize
data class MembershipLocal(
    var id: String = "",
    var userId: String? = null,
    var type: String? = null,
    var duration: Int? = null,
    var price: Long? = null,
    var image: List<String>? = null,
    var maxCoupon: Int? = null,
    var tnc: List<String>? = null,
    var createdAt: String? = null,

    // Additional fields not in the original JSON
    var purchaseDate: Date? = null,
    var expiryDate: Date? = null,
    var isActive: Boolean = false,
    var remainingCoupons: Int? = null,
    var lastUpdatedAt: Date = Date()
) : Parcelable