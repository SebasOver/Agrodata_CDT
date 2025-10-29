package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.Producer

@Dao
interface ProducerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(producer: Producer): Long

    @Query("SELECT * FROM producers ORDER BY created_at DESC")
    suspend fun getAll(): List<Producer>

    @Query("SELECT * FROM producers WHERE id = :id")
    suspend fun findById(id: Long): Producer?

    @Query("DELETE FROM producers")
    suspend fun clear()
}