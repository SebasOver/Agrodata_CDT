package com.example.softwareganadero.data

import androidx.room.*

@Entity(
    tableName = "pasture_inventories",
    indices = [Index("operator_name"), Index("created_at")]
)
data class PastureInventory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val healthy: Int,
    val sick: Int,
    val total: Int,
    @ColumnInfo(name = "operator_name") val operatorName: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)

