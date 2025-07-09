package com.example.pointgrapher.di

import com.example.pointgrapher.data.repository.ImageSaverRepositoryImpl
import com.example.pointgrapher.domain.repository.ImageSaverRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageModule {

    @Binds
    abstract fun bindImageSaver(imageSaverImpl: ImageSaverRepositoryImpl): ImageSaverRepository
}