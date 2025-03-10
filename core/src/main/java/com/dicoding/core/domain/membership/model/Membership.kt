package com.dicoding.core.domain.membership.model

import android.os.Parcelable
import com.dicoding.core.domain.user.model.User
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

data class SubscriptionHistory(
    val results: List<Subscription> = emptyList(),
    val page: Int = 1,
    val limit: Int = 10,
    val totalPages: Int = 0,
    val totalResults: Int = 0
)

data class Subscription(
    val id: String,
    val userId: SubscriptionUser?,
    val verificatorId: SubscriptionUser?,
    val subscriptionType: String,
    val subscriptionTypeId: String,
    val status: String,
    val payment: Int?,
    val paymentProof: String?,
    val startDate: String,
    val endDate: String,
    val createdAt: String
)

// You can reuse the existing User model or create a simplified one
data class SubscriptionUser(
    val id: String,
    val name: String,
    val isMember: Boolean
)

data class MembershipStats(
    val totalMembers: Int = 0,
    val membershipCounts: Map<String, Int> = emptyMap()
)