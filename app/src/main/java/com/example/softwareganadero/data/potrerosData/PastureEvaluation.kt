package com.example.softwareganadero.data.potrerosData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "pasture_evaluations", indices = [Index(value = ["created_at"])])
data class PastureEvaluation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rotation") val rotation: String?,        // NUEVO (único)
    @ColumnInfo(name = "paddock") val paddock: String?,          // NUEVO (único)
    @ColumnInfo(name = "height_entry") val heightEntry: String?,
    @ColumnInfo(name = "height_exit") val heightExit: String?,
    @ColumnInfo(name = "color_entry") val colorEntry: String?,
    @ColumnInfo(name = "color_exit") val colorExit: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)