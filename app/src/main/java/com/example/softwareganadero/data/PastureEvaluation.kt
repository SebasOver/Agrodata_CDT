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
    @ColumnInfo(name = "height_entry") val heightEntry: String?,     // altura de “Entrada”
    @ColumnInfo(name = "height_exit") val heightExit: String?,       // altura de “Salida”
    @ColumnInfo(name = "color") val color: String?,                  // verde intenso/normal/claro
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)