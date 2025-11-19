package com.example.softwareganadero.data.potrerosData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "supplements", indices = [Index("rotation"), Index("lot"), Index("created_at")])
data class Supplement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rotation") val rotation: String,
    @ColumnInfo(name = "lot") val lot: String,                // CAMBIO: String
    @ColumnInfo(name = "animals_count") val animalsCount: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "quantity") val quantity: Double,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)
