package com.dicoding.membership.core.utils.datamapper

import com.dicoding.core.data.source.remote.response.test.LoginTest
import com.dicoding.core.data.source.remote.response.test.RegisterTest
import com.dicoding.membership.core.domain.auth.tester.model.LoginDomain
import com.dicoding.membership.core.domain.auth.tester.model.LoginResponseDomain
import com.dicoding.membership.core.domain.auth.tester.model.RegisterResponseDomain


object AuthDataMapper {

    // Tester
    fun mapLoginTestToDomain(input: LoginTest): LoginResponseDomain {
        return LoginResponseDomain(
            data = input.loginResult?.let {
                LoginDomain(
                    userId = it.userId,
                    name = it.name,
                    token = it.token
                )
            },
            error = input.error ?: false,
            message = input.message.orEmpty()
        )
    }

    fun mapRegisterTestToDomain(input: RegisterTest): RegisterResponseDomain {
        return RegisterResponseDomain(
            error = input.error ?: false,
            message = input.message.orEmpty()
        )
    }
    // Tester
}