package com.example.softwareganadero.data

import androidx.room.*

@Entity(
    tableName = "pasture_inventories",
    indices = [Index("operator_name"), Index("created_at")]
)
data class PastureInventory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "lot") val lot: Int,                   // NUEVO
    @ColumnInfo(name = "healthy") val healthy: Int,
    @ColumnInfo(name = "sick") val sick: Int,
    @ColumnInfo(name = "total") val total: Int,
    @ColumnInfo(name = "operator_name") val operatorName: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)

