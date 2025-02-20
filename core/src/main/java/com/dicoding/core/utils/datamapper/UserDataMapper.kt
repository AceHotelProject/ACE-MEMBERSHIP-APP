package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.remote.response.user.MembershipResponse
import com.dicoding.core.data.source.remote.response.user.MerchantIdResponse
import com.dicoding.core.data.source.remote.response.user.ReferralTokenResponse
import com.dicoding.core.data.source.remote.response.user.SubscriptionTypeResponse
import com.dicoding.core.data.source.remote.response.user.UserListResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import com.dicoding.core.domain.user.model.Membership
import com.dicoding.core.domain.user.model.Merchant
import com.dicoding.core.domain.user.model.ReferralToken
import com.dicoding.core.domain.user.model.SubscriptionType
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.model.UserList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object UserDataMapper {
    fun mapResponsesToDomain(response: UserListResponse): UserList = UserList(
        data = response.data.map { mapUserToDomain(it) },
        page = response.page,
        limit = response.limit,
        totalPages = response.totalPages,
        totalResults = response.totalResults
    )

    fun mapUserToDomain(input: UserResponse): User = User(
        id = input.id ?: "",
        name = input.name ?: "",
        email = input.email ?: "",
        phone = input.phone,
        address = input.address,
        citizenNumber = input.citizenNumber,
        pathKTP = input.pathKTP,
        role = input.role,
        merchantId = when (val merchantResponse = input.merchantId) {
            is MerchantIdResponse.MerchantData -> Merchant(
                point = merchantResponse.point,
                refferalPoint = merchantResponse.refferalPoint,
                id = merchantResponse.id
            )
            is MerchantIdResponse.MerchantString -> Merchant(
                point = 0,
                refferalPoint = 0,
                id = merchantResponse.id
            )
            null -> null
        },
        androidId = input.androidId,
        couponUsed = input.couponUsed,
        point = input.point,
        refferalPoint = input.refferalPoint,
        referralPoint = input.referralPoint,
        isEmailVerified = input.isEmailVerified,
        isNumberVerified = input.isNumberVerified,
        isPhoneVerified = input.isPhoneVerified,
        isValidated = input.isValidated,
        uniqueCode = input.uniqueCode,
        referralToken = input.referralToken,
        membership = input.membership?.let { mapMembershipToDomain(it) },
        isMember = input.isMember,
        createdAt = input.createdAt
    )

    private fun mapMembershipToDomain(input: MembershipResponse): Membership = Membership(
        userId = input.userId,
        verificatorId = input.verificatorId,
        subscriptionType = mapSubscriptionTypeToDomain(input.subscriptionType),
        status = input.status,
        payment = input.payment,
        paymentProof = input.paymentProof,
        startDate = input.startDate,
        endDate = input.endDate,
        createdAt = input.createdAt,
        id = input.id
    )

    private fun mapSubscriptionTypeToDomain(input: SubscriptionTypeResponse?): SubscriptionType = SubscriptionType(
        type = input?.type ?: "",
        id = input?.id ?: ""
    )

    fun mapReferralTokenToDomain(response: ReferralTokenResponse): ReferralToken {
        return ReferralToken(
            token = response.token,
            userId = response.user,
            type = response.type,
            expires = response.expires?.let { parseDate(it) },
            isBlacklisted = response.blacklisted,
            createdAt = parseDate(response.createdAt),
            id = response.id
        )
    }

    private fun parseDate(dateString: String): Date {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .parse(dateString) ?: Date()
    }
}