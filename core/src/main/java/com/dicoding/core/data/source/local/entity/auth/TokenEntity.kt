package com.dicoding.core.data.source.local.entity.auth

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenEntity(
    @ColumnInfo(name = "accessToken")
    var accessToken: String? = "",

    @ColumnInfo(name = "accessTokenExpire")
    var accessTokenExpire: String? = "",

    @ColumnInfo(name = "refreshToken")
    var refreshToken: String? = "",

    @ColumnInfo(name = "refreshTokenExpire")
    var refreshTokenExpire: String? = "",
    ) : Parcelable