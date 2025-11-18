package com.example.softwareganadero.dao.corralesDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.corralesData.Palpation

@Dao
interface PalpationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: Palpation): Long

    @Query("SELECT * FROM palpations ORDER BY created_at_text ASC")
    suspend fun getAll(): List<Palpation>
}