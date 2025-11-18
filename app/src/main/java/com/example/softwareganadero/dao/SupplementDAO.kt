package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.Supplement

@Dao
interface SupplementDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: Supplement): Long

    @Query("SELECT * FROM supplements ORDER BY created_at_text ASC")
    suspend fun getAll(): List<Supplement>
}