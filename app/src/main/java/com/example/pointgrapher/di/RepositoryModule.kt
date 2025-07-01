package com.example.pointgrapher.di

import com.example.pointgrapher.data.repository.PointProviderRepositoryImpl
import com.example.pointgrapher.data.repository.UIFrameworkRepositoryImpl
import com.example.pointgrapher.domain.repository.PointProviderRepository
import com.example.pointgrapher.domain.repository.UIFrameworkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUIFrameworkRepository(
        uiFrameworkRepositoryImpl: UIFrameworkRepositoryImpl
    ): UIFrameworkRepository

    @Binds
    abstract fun bindPointProviderRepository(
        pointProviderRepositoryImpl: PointProviderRepositoryImpl
    ): PointProviderRepository
}