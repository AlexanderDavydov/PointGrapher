package com.example.pointgrapher.domain.repository

import com.example.pointgrapher.domain.model.UIFramework

interface UIFrameworkRepository {

    fun getUIFramework(): UIFramework

    fun setUIFramework(framework: UIFramework)

    fun clearUIFramework()
}