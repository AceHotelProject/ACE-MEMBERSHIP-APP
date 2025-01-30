package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.local.entity.auth.TokenEntity
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.OtpResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.OtpDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import com.dicoding.core.domain.auth.model.TokensDomain
import com.dicoding.core.domain.auth.model.TokensFormat
import com.dicoding.core.domain.auth.model.UserDomain

object AuthDataMapper {

    fun mapAuthToEntity(domain: LoginDomain): UserEntity {
        return UserEntity(
            userId = domain.user.id,
            role = domain.user.role,
            username = domain.user.name,
            email = domain.user.email,
            isEmailVerified = domain.user.isEmailVerified,
            tokenInfo = TokenEntity(
                accessToken = domain.tokens.accessToken.token,
                accessTokenExpire = domain.tokens.accessToken.expires,
                refreshToken = domain.tokens.refreshToken.token,
                refreshTokenExpire = domain.tokens.refreshToken.expires
            )
        )
    }

    fun mapRegisterResponseToDomain(response: RegisterResponse): RegisterDomain {
        return RegisterDomain(
            user = UserDomain(
                id = response.user?.id.orEmpty(),
                name = response.user?.name.orEmpty(),
                email = response.user?.email.orEmpty(),
                role = response.user?.role.orEmpty(),
                isEmailVerified = response.user?.isEmailVerified ?: false
            ),
            tokens = TokensDomain(
                accessToken = TokensFormat(
                    token = response.tokens?.access?.token,
                    expires = response.tokens?.access?.expires
                ),
                refreshToken = TokensFormat(
                    token = response.tokens?.refresh?.token,
                    expires = response.tokens?.refresh?.expires
                )
            )
        )
    }

    fun mapLoginResponseToDomain(response: LoginResponse): LoginDomain {
        return LoginDomain(
            user = UserDomain(
                id = response.user?.id.orEmpty(),
                name = response.user?.name.orEmpty(),
                email = response.user?.email.orEmpty(),
                role = response.user?.role.orEmpty(),
                isEmailVerified = response.user?.isEmailVerified ?: false
            ),
            tokens = TokensDomain(
                accessToken = TokensFormat(
                    token = response.tokens?.access?.token,
                    expires = response.tokens?.access?.expires
                ),
                refreshToken = TokensFormat(
                    token = response.tokens?.refresh?.token,
                    expires = response.tokens?.refresh?.expires
                )
            )
        )
    }

    fun mapUserEntityToDomain(entity: UserEntity?): LoginDomain {
        return entity?.let {
            LoginDomain(
                user = UserDomain(
                    id = it.userId,
                    name = it.username.orEmpty(),
                    email = it.email.orEmpty(),
                    role = it.role.orEmpty(),
                    isEmailVerified = false
                ),
                tokens = TokensDomain(
                    accessToken = TokensFormat(
                        token = it.tokenInfo.accessToken,
                        expires = it.tokenInfo.accessTokenExpire
                    ),
                    refreshToken = TokensFormat(
                        token = it.tokenInfo.refreshToken,
                        expires = it.tokenInfo.refreshTokenExpire
                    )
                )
            )
        } ?: LoginDomain(
            user = UserDomain(
                id = "",
                name = "",
                email = "",
                role = "",
                isEmailVerified = false
            ),
            tokens = TokensDomain(
                accessToken = TokensFormat(token = null, expires = null),
                refreshToken = TokensFormat(token = null, expires = null)
            )
        )
    }

    fun mapOtpResponseToDomain(input: OtpResponse): OtpDomain {
        return OtpDomain(
            expires = input.expires.orEmpty(),
            blacklisted = input.blacklisted ?: false,
            id = input.id.orEmpty(),
            type = input.type.orEmpty(),
            user = input.user.orEmpty(),
            token = input.token.orEmpty()
        )
    }
}