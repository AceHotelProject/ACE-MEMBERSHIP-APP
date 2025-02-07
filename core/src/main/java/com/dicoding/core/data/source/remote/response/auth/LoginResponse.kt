package com.dicoding.core.data.source.remote.response.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
	val tokens: Tokens? = null,
	val user: AuthUser? = null
) : Parcelable

@Parcelize
data class Tokens(
	val access: Access? = null,
	val refresh: Refresh? = null
) : Parcelable

@Parcelize
data class Refresh(
	val expires: String? = null,
	val token: String? = null
) : Parcelable

@Parcelize
data class Access(
	val expires: String? = null,
	val token: String? = null
) : Parcelable
