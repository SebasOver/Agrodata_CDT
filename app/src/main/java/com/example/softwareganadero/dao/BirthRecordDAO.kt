package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.softwareganadero.data.potrerosData.BirthRecord

@Dao
interface BirthRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BirthRecord): Long

    @Update
    suspend fun update(record: BirthRecord)

    @Query("SELECT * FROM birth_records WHERE id = :id")
    suspend fun getById(id: Long): BirthRecord?

    @Query("SELECT * FROM birth_records WHERE remote_id = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: String): BirthRecord?

    @Query("SELECT * FROM birth_records WHERE pending_sync = 1")
    suspend fun getPendingForSync(): List<BirthRecord>

    @Query("SELECT * FROM birth_records")
    suspend fun getAll(): List<BirthRecord>

}