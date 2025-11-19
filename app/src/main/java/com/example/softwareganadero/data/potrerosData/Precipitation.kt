package com.example.softwareganadero.data.potrerosData

import androidx.room.*

@Entity(
    tableName = "precipitations",
    indices = [Index("operator_name"), Index("created_at")]
)
data class Precipitation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "amount_mm") val amountMm: Double,
    @ColumnInfo(name = "operator_name") val operatorName: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "created_at_text") val createdAtText: String
)

