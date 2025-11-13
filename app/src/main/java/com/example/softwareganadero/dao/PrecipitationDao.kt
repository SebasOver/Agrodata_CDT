package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.softwareganadero.data.Precipitation

@Dao
interface PrecipitationDAO {
    @Insert
    suspend fun insert(p: Precipitation): Long
}
