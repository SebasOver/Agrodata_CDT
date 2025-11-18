package com.example.softwareganadero.dao.corralesDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.corralesData.Weighing

@Dao
interface WeighingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: Weighing): Long

    @Query("SELECT * FROM weighings ORDER BY created_at_text ASC")
    suspend fun getAll(): List<Weighing>
}