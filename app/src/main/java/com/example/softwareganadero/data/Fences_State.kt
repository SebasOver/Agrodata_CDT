package com.example.softwareganadero.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fences_states",
    indices = [Index(value = ["volteos"]), Index(value = ["created_at"])]
)
data class FenceState(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // Guarda el valor seleccionado del dropdown (1000, 3000, ...). Se usa String para alinearse al UI.
    @ColumnInfo(name = "volteos") val volteos: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)