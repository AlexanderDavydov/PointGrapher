package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.domain.model.UIFramework
import com.example.pointgrapher.domain.repository.UIFrameworkRepository
import javax.inject.Inject

class SetUIFrameworkUseCase @Inject constructor(
    private val uiFrameworkRepository: UIFrameworkRepository
) {
    operator fun invoke(framework: UIFramework) {
        uiFrameworkRepository.setUIFramework(framework)
    }
}