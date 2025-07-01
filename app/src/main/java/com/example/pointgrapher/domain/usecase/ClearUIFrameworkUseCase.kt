package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.domain.repository.UIFrameworkRepository
import javax.inject.Inject

class ClearUIFrameworkUseCase @Inject constructor(
    private val uiFrameworkRepository: UIFrameworkRepository
) {
    operator fun invoke() {
        uiFrameworkRepository.clearUIFramework()
    }
}