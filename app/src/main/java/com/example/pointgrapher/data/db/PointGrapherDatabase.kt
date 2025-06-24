package com.example.pointgrapher.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pointgrapher.data.db.dao.BatchDao
import com.example.pointgrapher.data.db.dao.PointDao
import com.example.pointgrapher.data.db.entity.BatchEntity
import com.example.pointgrapher.data.db.entity.PointEntity

@Database(
    entities = [BatchEntity::class, PointEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PointGrapherDatabase : RoomDatabase() {

    abstract fun batchDao(): BatchDao
    abstract fun pointDao(): PointDao
}