package com.example.softwareganadero.data.corralesData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weighings",
    indices = [Index("created_at"), Index("animal_number")]
)
data class Weighing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "sex") val sex: String,                    // "M" o "H"
    @ColumnInfo(name = "animal_number") val animalNumber: String, // solo dígitos
    @ColumnInfo(name = "breed") val breed: String,                // solo letras
    @ColumnInfo(name = "color") val color: String,                // solo letras
    @ColumnInfo(name = "body_condition") val bodyCondition: String, // texto o número
    @ColumnInfo(name = "observations") val observations: String?, // opcional
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)