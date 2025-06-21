package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.domain.model.PointBatch
import com.example.pointgrapher.domain.repository.PointProviderRepository
import javax.inject.Inject

class GetPointsBatchUseCase @Inject constructor(
    private val pointProviderRepository: PointProviderRepository
) {
    suspend operator fun invoke(batchId: String): PointBatch {
        return pointProviderRepository.getPointsBatch(batchId)
    }
}