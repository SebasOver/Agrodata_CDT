package com.example.softwareganadero.data.cultivosData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "crop_records",
    indices = [Index("created_at")]
)
data class CropRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "lot") val lot: String,              // letras y números, obligatorio
    @ColumnInfo(name = "species") val species: String,      // solo letras, obligatorio
    @ColumnInfo(name = "has_pests") val hasPests: Boolean,  // switch plagas
    @ColumnInfo(name = "has_diseases") val hasDiseases: Boolean, // switch enfermedades
    @ColumnInfo(name = "notes") val notes: String?,         // opcional
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)