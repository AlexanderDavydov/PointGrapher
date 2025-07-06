package com.example.pointgrapher

import com.example.pointgrapher.data.model.BatchDto
import com.example.pointgrapher.data.model.PointDto
import com.example.pointgrapher.data.model.PointsResponse
import com.example.pointgrapher.domain.model.BatchInfo
import com.example.pointgrapher.domain.model.PointBatch
import com.example.pointgrapher.presentation.result.model.PointViewData

object TestDataFactory {

    fun createPointDto(x: Double = 1.0, y: Double = 2.0) = PointDto(x, y)

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

    fun createBatchDto(
        id: String = "test-batch-id",
        actualCount: Int = 5,
        timestamp: Long = System.currentTimeMillis()
    ) = BatchDto(id, actualCount, timestamp)

    fun createBatchInfo(
        id: String = "test-batch-id",
        numberOfPoints: Int = 5,
        timestamp: Long = System.currentTimeMillis()
    ) = BatchInfo(id, numberOfPoints, timestamp)

    fun createPointViewData(count: Int = 5): PointViewData {
        val x = (1..count).map { it.toDouble() }
        val y = (1..count).map { it * 2.0 }
        return PointViewData(x, y)
    }

    object Edge {
        fun emptyPointBatch() = PointBatch(emptyList(), emptyList())

        fun singlePoint() = PointBatch(listOf(1.0), listOf(2.0))

        fun unsortedPoints() = PointBatch(
            x = listOf(3.0, 1.0, 2.0, 5.0, 4.0),
            y = listOf(6.0, 2.0, 4.0, 10.0, 8.0)
        )

        fun duplicateXValues() = PointBatch(
            x = listOf(1.0, 1.0, 2.0),
            y = listOf(2.0, 3.0, 4.0)
        )

        fun largeDataset() = createPointBatch(1000)
    }

    object Error {
        const val INVALID_BATCH_ID = "non-existent-batch"
        const val NETWORK_ERROR_MESSAGE = "Network error occurred"
        const val SERVER_ERROR_MESSAGE = "Internal server error"
        const val INVALID_POINT_COUNT_MESSAGE = "Incorrect point number"
    }
}