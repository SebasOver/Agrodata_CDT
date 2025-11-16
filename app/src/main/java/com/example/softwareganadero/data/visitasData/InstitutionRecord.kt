package com.example.softwareganadero.data.visitasData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "institution_records",
    indices = [Index("created_at"), Index("visitor_name")]
)
data class InstitutionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "visitor_name") val visitorName: String,   // solo letras
    @ColumnInfo(name = "reason") val reason: String,              // solo letras
    @ColumnInfo(name = "notes") val notes: String?,               // opcional
    @ColumnInfo(name = "created_at") val createdAt: Long,         // hora entrada
    @ColumnInfo(name = "created_at_text") val createdAtText: String,
    @ColumnInfo(name = "closed_at") val closedAt: Long?,          // hora salida
    @ColumnInfo(name = "closed_at_text") val closedAtText: String?
)