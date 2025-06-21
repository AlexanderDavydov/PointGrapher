package com.example.pointgrapher.di

import android.content.Context
import androidx.room.Room
import com.example.pointgrapher.data.db.PointGrapherDatabase
import com.example.pointgrapher.data.db.dao.BatchDao
import com.example.pointgrapher.data.db.dao.PointDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePointGrapherDatabase(
        @ApplicationContext context: Context
    ): PointGrapherDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            PointGrapherDatabase::class.java,
            "point_grapher_database"
        ).build()
    }

    @Provides
    fun provideBatchDao(database: PointGrapherDatabase): BatchDao {
        return database.batchDao()
    }

    @Provides
    fun providePointDao(database: PointGrapherDatabase): PointDao {
        return database.pointDao()
    }
}