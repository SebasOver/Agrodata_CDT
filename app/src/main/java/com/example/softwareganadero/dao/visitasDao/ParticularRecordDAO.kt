package com.example.softwareganadero.dao.visitasDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.BirthRecord
import com.example.softwareganadero.data.visitasData.ParticularRecord

@Dao
interface ParticularDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: ParticularRecord): Long

    @Query("SELECT * FROM particular_records WHERE closed_at IS NULL ORDER BY created_at DESC")
    suspend fun getOpen(): List<ParticularRecord>

    @Query(
        "UPDATE particular_records " +
                "SET closed_at = :closedAt, closed_at_text = :closedAtText " +
                "WHERE id = :id"
    )
    suspend fun closeVisit(id: Long, closedAt: Long, closedAtText: String): Int
    @Query("SELECT * FROM particular_records ORDER BY created_at_text ASC")
    suspend fun getAll(): List<ParticularRecord>
}