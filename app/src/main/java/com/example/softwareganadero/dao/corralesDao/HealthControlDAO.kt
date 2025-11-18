package com.example.softwareganadero.dao.corralesDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.corralesData.HealthControl
import com.example.softwareganadero.data.corralesData.Palpation

@Dao
interface HealthControlDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: HealthControl): Long

    @Query("SELECT * FROM health_control ORDER BY created_at_text ASC")
    suspend fun getAll(): List<HealthControl>
}