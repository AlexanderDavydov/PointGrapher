package com.example.pointgrapher.data.datasource

import com.example.pointgrapher.data.db.dao.BatchDao
import com.example.pointgrapher.data.db.dao.PointDao
import com.example.pointgrapher.data.db.entity.BatchEntity
import com.example.pointgrapher.data.db.entity.PointEntity
import com.example.pointgrapher.data.model.BatchDto
import com.example.pointgrapher.data.model.PointDto
import com.example.pointgrapher.domain.exception.BatchNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PointLocalDataSource @Inject constructor(
    private val batchDao: BatchDao, private val pointDao: PointDao
) {

    /**
     * Saves a batch of points to the local data source.
     *
     * @param batchId The ID of the batch.
     * @param points The list of points to save.
     */
    suspend fun saveBatch(batchId: String, points: List<PointDto>) {
        val timestamp = System.currentTimeMillis()

        val batchEntity = BatchEntity(
            id = batchId, actualCount = points.size, timestamp = timestamp
        )
        batchDao.insertBatch(batchEntity)

        val pointEntities = points.map { dto ->
            PointEntity(batchId = batchId, x = dto.x, y = dto.y)
        }
        pointDao.insertPoints(pointEntities)
    }

    suspend fun getBatch(batchId: String): List<PointDto> {
        batchDao.getBatchById(batchId) ?: throw BatchNotFoundException(batchId)

        val pointEntities = pointDao.getPointsByBatchId(batchId)

        return pointEntities.map { entity ->
            PointDto(x = entity.x, y = entity.y)
        }
    }

    fun observeAllBatches(): Flow<List<BatchDto>> {
        return batchDao.observeAllBatches().map { entitys ->
            entitys.map { entity ->
                BatchDto(
                    id = entity.id,
                    actualCount = entity.actualCount,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    suspend fun deleteBatch(batchId: String) {
        batchDao.getBatchById(batchId) ?: throw BatchNotFoundException(batchId)

        pointDao.deletePointsByBatchId(batchId)
        batchDao.deleteBatchById(batchId)
    }
}