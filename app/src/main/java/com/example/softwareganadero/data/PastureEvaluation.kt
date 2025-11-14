package com.example.softwareganadero.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pasture_evaluations",
    indices = [Index(value = ["created_at"])]
)
data class PastureEvaluation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "height_entry") val heightEntry: String?,     // Altura entrada
    @ColumnInfo(name = "height_exit") val heightExit: String?,       // Altura salida
    @ColumnInfo(name = "color_entry") val colorEntry: String?,       // Color entrada
    @ColumnInfo(name = "color_exit") val colorExit: String?,         // Color salida
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)