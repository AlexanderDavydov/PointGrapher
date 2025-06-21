package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.domain.model.BatchInfo
import com.example.pointgrapher.domain.repository.PointProviderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBatchesUseCase @Inject constructor(
    private val pointProviderRepository: PointProviderRepository
) {
    operator fun invoke(): Flow<List<BatchInfo>> {
        return pointProviderRepository.observeAllBatches()
    }
}