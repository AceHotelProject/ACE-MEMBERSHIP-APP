package com.dicoding.core.domain.points.repository

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import kotlinx.coroutines.flow.Flow

interface IPointsRepository {

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