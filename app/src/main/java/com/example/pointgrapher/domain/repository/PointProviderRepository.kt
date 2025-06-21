package com.example.pointgrapher.domain.repository

import com.example.pointgrapher.domain.model.BatchInfo
import com.example.pointgrapher.domain.model.PointBatch


interface PointProviderRepository {

    /**
     * Requests a batch of points from the data source.
     *
     * @param count The number of points to request.
     * @return batchId string representing the batch of points.
     */
    suspend fun requestPoints(count: Int): String

    /**
     * Retrieves a batch of points from the data source.
     *
     * @param batchId The ID of the batch to retrieve.
     * @return The batch of points.
     * @throws BatchNotFoundException if the batch is not found.
     */
    suspend fun getPointsBatch(batchId: String): PointBatch


    /**
     * Retrieves all batches of points from the data source.
     *
     * @return A list of batches.
     */
    suspend fun getAllBatches(): List<BatchInfo>

    /**
     * Deletes a batch of points from the data source.
     *
     * @param batchId The ID of the batch to delete.
     * @throws [BatchNotFoundException] if the batch is not found.
     */
    suspend fun deleteBatch(batchId: String)
}