package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.domain.model.UIFramework
import com.example.pointgrapher.domain.repository.UIFrameworkRepository
import javax.inject.Inject

class GetUIFrameworkUseCase @Inject constructor(
    private val uiFrameworkRepository: UIFrameworkRepository
) {
    operator fun invoke(): UIFramework {
        return uiFrameworkRepository.getUIFramework()
    }
}