package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.domain.exeption.NerworkError
import com.example.pointgrapher.domain.model.Point
import com.example.pointgrapher.domain.repository.PointProviderRepository
import javax.inject.Inject

class GetPointsUsecase @Inject constructor(
    private val pointProviderRepository: PointProviderRepository
) {
    operator suspend fun invoke(count: Int): List<Point> {
        return try {
            pointProviderRepository.getPoints(count)
        } catch (e: Exception) {
            throw NerworkError(e)
        }
    }
}