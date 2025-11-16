package com.example.softwareganadero.dao.cultivosDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.cultivosData.CropRecord

@Dao
interface CropDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: CropRecord): Long

    @Query("SELECT * FROM crop_records ORDER BY created_at DESC")
    suspend fun getAll(): List<CropRecord>
}