package com.dicoding.core.domain.auth.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginDomain(
    val user: UserDomain,
    val tokens: TokensDomain
) : Parcelable