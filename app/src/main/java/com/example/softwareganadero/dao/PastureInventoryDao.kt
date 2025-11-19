package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.softwareganadero.data.potrerosData.PastureInventory

@Dao
interface PastureInventoryDAO {
    @Insert
    suspend fun insert(p: PastureInventory): Long

    @Query("SELECT * FROM pasture_inventories ORDER BY created_at_text ASC")
    suspend fun getAll(): List<PastureInventory>
}
