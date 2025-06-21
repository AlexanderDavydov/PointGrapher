package com.example.pointgrapher.data.repository

import com.example.pointgrapher.data.datasource.PointLocalDataSource
import com.example.pointgrapher.data.datasource.PointRemoteDataSource
import com.example.pointgrapher.domain.model.BatchInfo
import com.example.pointgrapher.domain.model.PointBatch
import com.example.pointgrapher.domain.repository.PointProviderRepository
import java.util.UUID
import javax.inject.Inject

class PointProviderRepositoryImpl @Inject constructor(
    private val pointRemoteDataSource: PointRemoteDataSource,
    private val pointLocalDataSource: PointLocalDataSource
) : PointProviderRepository {

    override suspend fun requestPoints(count: Int): String {
        val pointDtos = pointRemoteDataSource.getPoints(count)

        val batchId = UUID.randomUUID().toString()
        pointLocalDataSource.saveBatch(batchId, pointDtos)

        return batchId
    }

    override suspend fun getPointsBatch(batchId: String): PointBatch {
        val pointDtos =
            pointLocalDataSource.getBatch(batchId)

        val xCoords = pointDtos.map { it.x }
        val yCoords = pointDtos.map { it.y }

        return PointBatch(x = xCoords, y = yCoords)
    }

    override suspend fun getAllBatches(): List<BatchInfo> {
        return pointLocalDataSource.getAllBatches()
            .map {
                BatchInfo(id = it.id, numberOfPoints = it.actualCount, timestamp = it.timestamp)
            }
    }

    override suspend fun deleteBatch(batchId: String) {
        pointLocalDataSource.deleteBatch(batchId)
    }
}