package com.dicoding.core.domain.points.usecase

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.PointHistory1
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.model.UserPointHistory
import kotlinx.coroutines.flow.Flow

interface PointsUseCase {
    fun getUserPoints(userId: String): Flow<Resource<Points>>

    fun transferPoints(to: String, from: String, amount: Int, notes: String): Flow<Resource<PointHistory1>>

    fun getUserHistory(userId: String): Flow<Resource<UserPointHistory>>
}