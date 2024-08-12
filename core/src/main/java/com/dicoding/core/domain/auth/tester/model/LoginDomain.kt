package com.dicoding.membership.core.domain.auth.tester.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginDomain(
    val userId: String?,
    val name: String?,
    val token: String?
) : Parcelable