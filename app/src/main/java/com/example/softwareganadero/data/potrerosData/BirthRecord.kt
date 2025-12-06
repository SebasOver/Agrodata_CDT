package com.example.softwareganadero.data.potrerosData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "birth_records",
    indices = [
        Index(value = ["cow_tag"]),
        Index(value = ["operator_name"]),
        Index(value = ["remote_id"])
    ]
)
data class BirthRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    // --- Campos de sincronización ---
    @ColumnInfo(name = "remote_id") val remoteId: String? = null, // id del doc en Firestore
    @ColumnInfo(name = "updated_at_millis") val updatedAtMillis: Long,
    @ColumnInfo(name = "pending_sync") val pendingSync: Boolean = true,
    @ColumnInfo(name = "deleted") val deleted: Boolean = false,

    // --- Campos de tu modelo original ---
    @ColumnInfo(name = "cow_tag") val cowTag: String,          // tag de la vaca
    @ColumnInfo(name = "calf_tag") val calfTag: String,        // número de cría
    @ColumnInfo(name = "sex") val sex: String,                 // "M" o "H"
    @ColumnInfo(name = "color") val color: String?,
    @ColumnInfo(name = "weight") val weight: String?,          // texto libre
    @ColumnInfo(name = "colostrum") val colostrum: Boolean,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "operator_name") val operatorName: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)