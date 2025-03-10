package com.dicoding.core.utils.datamapper

import android.util.Log
import com.dicoding.core.data.source.local.entity.auth.TokenEntity
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import com.dicoding.core.data.source.local.entity.membership.MembershipEntity
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.data.source.remote.response.membership.SubscriptionHistoryResponse
import com.dicoding.core.data.source.remote.response.membership.SubscriptionItem
import com.dicoding.core.data.source.remote.response.user.UserResponse
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import com.dicoding.core.domain.auth.model.TokensDomain
import com.dicoding.core.domain.auth.model.TokensFormat
import com.dicoding.core.domain.auth.model.UserDomain
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.model.MembershipLocal
import com.dicoding.core.domain.membership.model.Subscription
import com.dicoding.core.domain.membership.model.SubscriptionHistory
import com.dicoding.core.domain.membership.model.SubscriptionUser
import com.dicoding.core.domain.user.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.Date

object MembershipDataMapper {
    fun mapResponseToDomain(input: MembershipResponse): Membership = Membership(
        id = input.id ?: "",
        type = input.type ?: "",
        duration = input.duration ?: 0,
        maxCoupon = input.maxCoupon ?: 0,
        price = input.price ?: 0,
        tnc = input.tnc ?: emptyList(),
        image = input.image ?: emptyList(),
        createdAt = input.createdAt ?: ""
    )

    fun toEntity(membership: MembershipLocal): MembershipEntity {
        // Calculate expiry date based on current date + duration (if available)
        val purchaseDate = membership.purchaseDate ?: Date()
        val expiryDate = if (membership.duration != null) {
            if (membership.expiryDate != null) {
                membership.expiryDate
            } else {
                Calendar.getInstance().apply {
                    time = purchaseDate
                    add(Calendar.DAY_OF_YEAR, membership.duration!!)
                }.time
            }
        } else {
            membership.expiryDate
        }

        // Use Gson for serializing the lists to JSON
        val gson = Gson()
        val imageJson = gson.toJson(membership.image)
        val tncJson = gson.toJson(membership.tnc)

        Log.d("MembershipMapper", "Storing image list: ${membership.image}")
        Log.d("MembershipMapper", "Serialized to JSON: $imageJson")

        return MembershipEntity(
            id = membership.id,
            userId = membership.userId.toString(),
            type = membership.type,
            duration = membership.duration,
            price = membership.price,
            maxCoupon = membership.maxCoupon,
            createdAt = membership.createdAt,
            imageJson = imageJson,
            tncJson = tncJson,
            purchaseDateMillis = purchaseDate.time,
            expiryDateMillis = expiryDate?.time,
            isActive = membership.isActive,
            remainingCoupons = membership.remainingCoupons ?: membership.maxCoupon
        )
    }

    // Convert from MembershipEntity to Membership domain model
    fun toDomain(entity: MembershipEntity): MembershipLocal {
        // Use Gson for deserializing the JSON strings back to lists
        val gson = Gson()

        Log.d("MembershipMapper", "Reading image JSON: ${entity.imageJson}")

        val imageList = try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(entity.imageJson, type)
        } catch (e: Exception) {
            Log.e("MembershipMapper", "Error parsing image JSON: ${e.message}")
            null
        }

        val tncList = try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(entity.tncJson, type)
        } catch (e: Exception) {
            Log.e("MembershipMapper", "Error parsing TnC JSON: ${e.message}")
            null
        }

        // Convert timestamp to Date
        val purchaseDate = entity.purchaseDateMillis?.let { Date(it) }
        val expiryDate = entity.expiryDateMillis?.let { Date(it) }

        Log.d("MembershipMapper", "Parsed image list: $imageList")

        return MembershipLocal(
            id = entity.id,
            userId = entity.userId,
            type = entity.type,
            duration = entity.duration,
            price = entity.price,
            image = imageList,
            maxCoupon = entity.maxCoupon,
            tnc = tncList,
            createdAt = entity.createdAt,
            purchaseDate = purchaseDate,
            expiryDate = expiryDate,
            isActive = entity.isActive,
            remainingCoupons = entity.remainingCoupons,
            lastUpdatedAt = Date() // Set current date as last updated
        )
    }

    fun domainToLocal(membership: com.dicoding.core.domain.membership.model.Membership, userId: String?): MembershipLocal {
        // Calculate purchase date (current date) and expiry date based on duration
        val purchaseDate = Date()
        val expiryDate = Calendar.getInstance().apply {
            time = purchaseDate
            add(Calendar.DAY_OF_YEAR, membership.duration)
        }.time

        return MembershipLocal(
            id = membership.id,
            userId = userId,
            type = membership.type,
            duration = membership.duration,
            price = membership.price.toLong(),
            image = membership.image,
            maxCoupon = membership.maxCoupon,
            tnc = membership.tnc,
            createdAt = membership.createdAt,
            purchaseDate = purchaseDate,
            expiryDate = expiryDate,
            isActive = true, // New memberships are active by default
            remainingCoupons = membership.maxCoupon, // Start with max coupons
            lastUpdatedAt = Date() // Current time
        )
    }

    fun mapSubscriptionHistoryResponseToDomain(input: SubscriptionHistoryResponse): SubscriptionHistory {
        return SubscriptionHistory(
            results = input.results.map { mapSubscriptionItemToDomain(it) },
            page = input.page,
            limit = input.limit,
            totalPages = input.totalPages,
            totalResults = input.totalResults
        )
    }

    private fun mapSubscriptionItemToDomain(input: SubscriptionItem): Subscription {
        return Subscription(
            id = input.id ?: "",
            userId = input.userId?.let {
                SubscriptionUser(
                    id = it.id ?: "",
                    name = it.name ?: "",
                    isMember = it.isMember
                )
            },
            verificatorId = input.verificatorId?.let {
                SubscriptionUser(
                    id = it.id ?: "",
                    name = it.name ?: "",
                    isMember = it.isMember
                )
            },
            subscriptionType = input.subscriptionType?.type ?: "",
            subscriptionTypeId = input.subscriptionType?.id ?: "",
            status = input.status ?: "",
            payment = input.payment,
            paymentProof = input.paymentProof,
            startDate = input.startDate ?: "",
            endDate = input.endDate ?: "",
            createdAt = input.createdAt ?: ""
        )
    }


}