package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.softwareganadero.data.Precipitation
import com.example.softwareganadero.data.Supplement

@Dao
interface PrecipitationDAO {
    @Insert
    suspend fun insert(p: Precipitation): Long
    @Query("SELECT * FROM precipitations ORDER BY created_at_text ASC")
    suspend fun getAll(): List<Precipitation>
}
