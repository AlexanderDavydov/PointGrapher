package com.example.pointgrapher.domain.repository

import com.example.pointgrapher.domain.model.Point


interface PointProviderRepository {
    suspend fun getPoints(count: Int): List<Point>
}