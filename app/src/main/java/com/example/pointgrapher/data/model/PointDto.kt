package com.example.pointgrapher.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PointDto(
    val x: Double,
    val y: Double
)
