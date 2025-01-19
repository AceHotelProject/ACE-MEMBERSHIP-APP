package com.dicoding.core.domain.points.usecase

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface PointsUseCase {
    fun getUserPoints(userId: String): Flow<Resource<Points>>

    fun getUserPointHistory(userId: String): Flow<Resource<List<PointHistory>>>

    fun transferPoints(
        userId: String,
        userReceiverId: String,
        amount: Int,
        note: String,
        purpose: String
    ): Flow<Resource<Unit>>
}