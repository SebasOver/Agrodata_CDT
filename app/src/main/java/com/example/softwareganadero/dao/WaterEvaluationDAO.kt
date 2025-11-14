package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.WaterEvaluation

@Dao
interface WaterEvaluationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: WaterEvaluation): Long

    @Query("SELECT * FROM water_evaluations ORDER BY created_at DESC")
    suspend fun getAll(): List<WaterEvaluation>
}