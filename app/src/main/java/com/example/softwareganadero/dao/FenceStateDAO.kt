package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.FenceState

@Dao
interface FenceStateDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: FenceState): Long

    @Query("SELECT * FROM fences_states ORDER BY created_at DESC")
    suspend fun getAll(): List<FenceState>

    @Query("SELECT COUNT(*) FROM fences_states")
    suspend fun count(): Long
}