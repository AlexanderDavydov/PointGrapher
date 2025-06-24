package com.example.pointgrapher.data.api

import com.example.pointgrapher.data.model.PointsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PointApi {

    /**
     * Retrieves a list of points from the server.
     *
     * @param count The number of points to retrieve.
     * @return A response containing the list of points.
     */
    @GET("/api/test/points")
    suspend fun getPoints(
        @Query("count") count: Int
    ): Response<PointsResponse>
}
