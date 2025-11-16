package com.example.softwareganadero.data.corralesData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "health_control", indices = [Index("created_at")])
data class HealthControl(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "treatment") val treatment: String,
    @ColumnInfo(name = "animal") val animal: String,
    @ColumnInfo(name = "medicines") val medicines: String,
    @ColumnInfo(name = "dose") val dose: String,
    @ColumnInfo(name = "quantity") val quantity: String, // guarda texto numérico; si prefieres REAL cámbialo y migra
    @ColumnInfo(name = "observations") val observations: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)