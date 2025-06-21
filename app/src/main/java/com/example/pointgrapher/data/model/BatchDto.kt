package com.example.pointgrapher.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BatchDto(
    val id: String,
    val actualCount: Int,
    val timestamp: Long
)
