package com.example.softwareganadero.data.corralesData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "palpations",
    indices = [Index("created_at"), Index("animal_number")]
)
data class Palpation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "animal_number") val animalNumber: String, // solo dígitos
    @ColumnInfo(name = "pregnancy_days") val pregnancyDays: Int,  // solo dígitos
    @ColumnInfo(name = "observations") val observations: String?, // opcional
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)