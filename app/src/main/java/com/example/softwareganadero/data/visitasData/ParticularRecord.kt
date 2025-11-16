package com.example.softwareganadero.data.visitasData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "particular_records",
    indices = [Index("created_at"), Index("visitor_name")]
)
data class ParticularRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "visitor_name") val visitorName: String,
    @ColumnInfo(name = "reason") val reason: String,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String,
    @ColumnInfo(name = "closed_at") val closedAt: Long?,
    @ColumnInfo(name = "closed_at_text") val closedAtText: String?
)