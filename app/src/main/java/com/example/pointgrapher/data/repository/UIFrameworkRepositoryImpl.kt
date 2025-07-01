package com.example.pointgrapher.data.repository

import com.example.pointgrapher.data.model.UIFrameworkDto
import com.example.pointgrapher.domain.model.UIFramework
import com.example.pointgrapher.domain.repository.UIFrameworkRepository
import com.example.pointgrapher.presentation.xmlui.UIFrameworkDataSource
import javax.inject.Inject

class UIFrameworkRepositoryImpl @Inject constructor(
    private val dataSource: UIFrameworkDataSource
) : UIFrameworkRepository {

    override fun getUIFramework(): UIFramework {
        return when (dataSource.getSelectedFramework()) {
            UIFrameworkDto.COMPOSE -> UIFramework.COMPOSE
            UIFrameworkDto.XML -> UIFramework.XML
            UIFrameworkDto.NONE -> UIFramework.NONE
        }
    }

    override fun setUIFramework(framework: UIFramework) {
        dataSource.saveSelectedFramework(
            when (framework) {
                UIFramework.COMPOSE -> UIFrameworkDto.COMPOSE
                UIFramework.XML -> UIFrameworkDto.XML
                UIFramework.NONE -> UIFrameworkDto.NONE
            }
        )
    }

    override fun clearUIFramework() {
        dataSource.clearSelection()
    }
}