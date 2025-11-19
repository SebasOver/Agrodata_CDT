package com.example.softwareganadero.data.potrerosData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pasture_fence_logs",
    indices = [
        Index(value = ["rotacion"]),
        Index(value = ["potrero"]),
        Index(value = ["volteos"]),
        Index(value = ["created_at"])
    ]
)
data class PastureFenceLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rotacion") val rotacion: String,     // requerido
    @ColumnInfo(name = "potrero") val potrero: String,       // requerido
    @ColumnInfo(name = "volteos") val volteos: String,       // requerido (dropdown)
    @ColumnInfo(name = "notes") val notes: String?,          // opcional
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)