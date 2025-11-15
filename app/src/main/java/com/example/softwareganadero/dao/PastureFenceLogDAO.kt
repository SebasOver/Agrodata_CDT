package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.PastureFenceLog

@Dao
interface PastureFenceLogDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: PastureFenceLog): Long

    @Query("SELECT * FROM pasture_fence_logs ORDER BY created_at DESC")
    suspend fun getAll(): List<PastureFenceLog>
}