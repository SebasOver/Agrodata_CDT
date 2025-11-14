package com.example.softwareganadero.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grazings",
    indices = [Index(value = ["rotacion"]), Index(value = ["potrero"]), Index(value = ["created_at"])]
)
data class Grazing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rotacion") val rotacion: String,
    @ColumnInfo(name = "potrero") val potrero: String,
    @ColumnInfo(name = "animals_count") val animalsCount: Int,   // NUEVO
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)