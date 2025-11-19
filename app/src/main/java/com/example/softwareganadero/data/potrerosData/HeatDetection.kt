package com.example.softwareganadero.data.potrerosData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "heat_detections",
    indices = [Index(value = ["cow_tag"]), Index(value = ["created_at"])]
)
data class HeatDetection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "in_heat") val inHeat: Boolean,           // switch
    @ColumnInfo(name = "cow_tag") val cowTag: String?,           // null si no hay celo
    @ColumnInfo(name = "notes") val notes: String?,              // opcional pero validado en UI
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)
