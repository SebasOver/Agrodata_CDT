package com.example.softwareganadero.data.potrerosData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "water_evaluations",
    indices = [Index(value = ["created_at"])]
)
data class WaterEvaluation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "availability") val availability: String,     // Escaso/Normal/Suficiente
    @ColumnInfo(name = "temperature") val temperature: Double,       // numérico
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)