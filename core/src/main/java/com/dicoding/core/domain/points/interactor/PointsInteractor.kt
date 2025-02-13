package com.dicoding.core.domain.points.interactor

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.model.UserPointHistory
import com.dicoding.core.domain.points.repository.IPointsRepository
import com.dicoding.core.domain.points.usecase.PointsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PointsInteractor @Inject constructor(
    private val pointsRepository: IPointsRepository
) : PointsUseCase {
    override fun getUserPoints(userId: String): Flow<Resource<Points>> {
        return pointsRepository.getUserPoints(userId)
    }
    override fun transferPoints(
        to: String,
        from: String,
        amount: Int,
        notes: String
    ): Flow<Resource<PointHistory>> {
        return pointsRepository.transferPoints(to, from, amount, notes)
    }
    override fun getUserHistory(userId: String): Flow<Resource<UserPointHistory>> {
        return pointsRepository.getUserHistory(userId)
    }
}