package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.domain.exception.NetworkError
import com.example.pointgrapher.domain.repository.PointProviderRepository
import javax.inject.Inject

class RequestPointsBatchUseCase @Inject constructor(
    private val pointProviderRepository: PointProviderRepository
) {
    suspend operator fun invoke(count: Int): String {
        return try {
            pointProviderRepository.requestPoints(count)
        } catch (e: Exception) {
            throw NetworkError(e)
        }
    }
}