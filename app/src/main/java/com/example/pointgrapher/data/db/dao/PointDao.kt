package com.example.pointgrapher.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pointgrapher.data.db.entity.PointEntity

@Dao
interface PointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<PointEntity>)

    @Query("SELECT * FROM points WHERE batch_id = :batchId ORDER BY id")
    suspend fun getPointsByBatchId(batchId: String): List<PointEntity>

    @Query("DELETE FROM points WHERE batch_id = :batchId")
    suspend fun deletePointsByBatchId(batchId: String)
}