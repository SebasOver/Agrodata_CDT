package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.FemaleCow

@Dao
interface FemaleCowDao {
    @Query("SELECT * FROM female_cows WHERE tag = :tag COLLATE NOCASE AND active = 1 LIMIT 1")
    suspend fun findActiveByTag(tag: String): FemaleCow?

    @Query("SELECT * FROM female_cows WHERE active = 1 ORDER BY tag COLLATE NOCASE")
    suspend fun listActive(): List<FemaleCow>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<FemaleCow>)
}