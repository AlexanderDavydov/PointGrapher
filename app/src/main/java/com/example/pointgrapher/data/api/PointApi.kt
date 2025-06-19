package com.example.pointgrapher.data.api

import com.example.pointgrapher.data.model.PointsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PointApi {
    @GET("/api/test/points")
    suspend fun getPoints(
        @Query("count") count: Int
    ): Response<PointsResponse>
}
