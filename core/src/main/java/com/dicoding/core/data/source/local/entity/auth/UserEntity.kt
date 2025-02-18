package com.dicoding.core.data.source.local.entity.auth

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var userId: String,

    @ColumnInfo(name = "role")
    var role: String? = "",

    @ColumnInfo(name = "username")
    var username: String? = "",

    @ColumnInfo(name = "email")
    var email: String? = "",

    @ColumnInfo(name = "is_email_verified")
    var isEmailVerified: Boolean? = false,

    @Embedded
    var tokenInfo: TokenEntity,

    @Embedded
    var merchantInfo: MerchantIdEntity? = null

) : Parcelable

@Parcelize
data class MerchantIdEntity(
    @ColumnInfo(name = "merchant_id")
    var id: String? = null,

    @ColumnInfo(name = "merchant_point")
    var point: Int? = 0,

    @ColumnInfo(name = "merchant_referral_point")
    var referralPoint: Int? = 0
) : Parcelable