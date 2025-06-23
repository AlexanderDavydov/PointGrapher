package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.domain.repository.PointProviderRepository
import javax.inject.Inject

class DeletePointBatchUseCase @Inject constructor(
    private val pointProviderRepository: PointProviderRepository
) {
    suspend operator fun invoke(batchId: String) {
        pointProviderRepository.deleteBatch(batchId)
    }
}