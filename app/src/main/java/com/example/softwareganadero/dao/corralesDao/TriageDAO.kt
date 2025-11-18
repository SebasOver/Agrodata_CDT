package com.example.softwareganadero.dao.corralesDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.corralesData.TriageRecord

@Dao
interface TriageDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: TriageRecord): Long

    @Query("SELECT * FROM triage_records ORDER BY created_at_text ASC")
    suspend fun getAll(): List<TriageRecord>
}