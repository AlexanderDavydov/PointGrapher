package com.example.pointgrapher.data.datasource

import com.example.pointgrapher.data.api.PointApi
import com.example.pointgrapher.data.model.PointDto
import javax.inject.Inject

class PointRemoteDataSource @Inject constructor(
    private val pointApi: PointApi
) {

    /**
     * Retrieves a list of points from the remote data source.
     *
     * @param count The number of points to retrieve.
     * @return A list of points.
     * @throws Exception if the request fails.
     * @throws IllegalArgumentException if the point number is incorrect.
     * @throws IllegalStateException if the response is not successful.
     */
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
