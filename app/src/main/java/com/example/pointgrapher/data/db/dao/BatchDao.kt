package com.example.pointgrapher.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pointgrapher.data.db.entity.BatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntity)

    @Query("SELECT * FROM batches WHERE id = :batchId")
    suspend fun getBatchById(batchId: String): BatchEntity?

    @Query("SELECT * FROM batches ORDER BY timestamp DESC")
    fun observeAllBatches(): Flow<List<BatchEntity>>

    @Query("DELETE FROM batches WHERE id = :batchId")
    suspend fun deleteBatchById(batchId: String)
}