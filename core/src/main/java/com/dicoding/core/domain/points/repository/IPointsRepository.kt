package com.dicoding.core.domain.points.repository

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.model.UserPointHistory
import kotlinx.coroutines.flow.Flow


interface IPointsRepository {
    fun getUserPoints(userId: String): Flow<Resource<Points>>

    fun transferPoints(to: String, from: String, amount: Int, notes: String): Flow<Resource<PointHistory>>

    fun getUserHistory(userId: String): Flow<Resource<UserPointHistory>>
}