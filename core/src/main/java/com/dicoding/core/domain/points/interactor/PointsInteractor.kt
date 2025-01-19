package com.dicoding.core.domain.points.interactor

import com.dicoding.core.data.repository.PointsRepository
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.usecase.PointsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PointsInteractor @Inject constructor(
    private val pointsRepository: PointsRepository
) : PointsUseCase {
    override fun getUserPoints(userId: String): Flow<Resource<Points>> {
        return pointsRepository.getUserPoints(userId)
    }

    override fun getUserPointHistory(userId: String): Flow<Resource<List<PointHistory>>> {
        return pointsRepository.getUserPointHistory(userId)
    }

    override fun transferPoints(
        userId: String,
        userReceiverId: String,
        amount: Int,
        note: String,
        purpose: String
    ): Flow<Resource<Unit>> {
        return pointsRepository.transferPoints(
            userId = userId,
            userReceiverId = userReceiverId,
            amount = amount,
            note = note,
            purpose = purpose
        )
    }
}