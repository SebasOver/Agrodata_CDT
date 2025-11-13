package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.softwareganadero.data.PastureInventory

@Dao
interface PastureInventoryDAO {
    @Insert
    suspend fun insert(p: PastureInventory): Long
}
