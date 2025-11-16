package com.example.softwareganadero.dao.corralesDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.softwareganadero.data.corralesData.HealthControl

@Dao
interface HealthControlDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: HealthControl): Long
}