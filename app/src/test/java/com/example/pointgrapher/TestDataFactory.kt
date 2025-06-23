package com.example.pointgrapher

import com.example.pointgrapher.data.model.PointDto
import com.example.pointgrapher.data.model.PointsResponse
import com.example.pointgrapher.domain.model.PointBatch

object TestDataFactory {

    fun createPointDtoList(count: Int = 5): List<PointDto> {
        return (1..count).map { i ->
            PointDto(x = i.toDouble(), y = i * 2.0)
        }
    }

    fun createPointsResponse(count: Int = 5) = PointsResponse(
        points = createPointDtoList(count)
    )

    fun createPointBatch(count: Int = 5): PointBatch {
        val points = createPointDtoList(count)
        return PointBatch(
            x = points.map { it.x },
            y = points.map { it.y }
        )
    }

    object Error {
        const val INVALID_BATCH_ID = "non-existent-batch"
    }
}