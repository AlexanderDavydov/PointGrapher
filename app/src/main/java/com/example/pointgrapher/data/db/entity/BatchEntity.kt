package com.example.pointgrapher.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "actual_count")
    val actualCount: Int,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)