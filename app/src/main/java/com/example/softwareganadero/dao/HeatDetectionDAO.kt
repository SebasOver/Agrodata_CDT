package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.HeatDetection

@Dao
interface HeatDetectionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: HeatDetection): Long

    @Query("SELECT * FROM heat_detections ORDER BY created_at_text ASC")
    suspend fun getAll(): List<HeatDetection>
}