package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.BirthRecord

@Dao
interface BirthRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: BirthRecord)

    @Query("SELECT * FROM birth_records ORDER BY created_at DESC")
    suspend fun listAll(): List<BirthRecord>
}