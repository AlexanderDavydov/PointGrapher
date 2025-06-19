package com.example.pointgrapher.data.datasource

import com.example.pointgrapher.data.api.PointApi
import com.example.pointgrapher.data.model.PointDto
import javax.inject.Inject

class PointDataSource @Inject constructor(
    private val pointApi: PointApi
) {
    suspend fun getPoints(count: Int): List<PointDto> {
        val response = pointApi.getPoints(count)
        if (response.isSuccessful) {
            return response.body()?.points ?: emptyList()
        } else {
            throw if (response.code() == 400) {
                IllegalArgumentException("Incorrect point number")
            } else {
                Exception("Failed to fetch points. Error code: ${response.code()}")
            }
        }
    }
}
