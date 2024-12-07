package com.dicoding.core.domain.auth.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokensDomain(
    val accessToken: TokensFormat,
    val refreshToken: TokensFormat,
) : Parcelable

@Parcelize
data class TokensFormat(
    val token: String?,
    val expires: String?,
) : Parcelable