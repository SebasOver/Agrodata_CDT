package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.Grazing

@Dao
interface GrazingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: Grazing): Long

    @Query("SELECT * FROM grazings ORDER BY created_at DESC")
    suspend fun getAll(): List<Grazing>

    @Query("SELECT COUNT(*) FROM grazings")
    suspend fun count(): Long
}