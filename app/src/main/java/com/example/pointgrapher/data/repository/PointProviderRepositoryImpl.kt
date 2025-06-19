package com.example.pointgrapher.data.repository

import com.example.pointgrapher.data.datasource.PointDataSource
import com.example.pointgrapher.domain.model.Point
import com.example.pointgrapher.domain.repository.PointProviderRepository
import javax.inject.Inject

class PointProviderRepositoryImpl @Inject constructor(
    private val pointDataSource: PointDataSource
) : PointProviderRepository {
    override suspend fun getPoints(count: Int): List<Point> {
        return pointDataSource.getPoints(count)
            .map { dto -> Point(dto.x, dto.y) }
    }
}