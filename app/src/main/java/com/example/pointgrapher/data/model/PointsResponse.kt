package com.example.pointgrapher.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PointsResponse(
    val points: List<PointDto>
)