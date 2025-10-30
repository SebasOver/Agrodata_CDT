package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE full_name = :name COLLATE NOCASE AND active = 1 LIMIT 1")
    suspend fun findActiveByName(name: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<User>)

    @Query("SELECT * FROM users WHERE active = 1 ORDER BY full_name COLLATE NOCASE")
    suspend fun listActive(): List<User>
}
