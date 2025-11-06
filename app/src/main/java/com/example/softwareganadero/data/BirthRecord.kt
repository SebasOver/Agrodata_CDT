package com.example.softwareganadero.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "birth_records",
    indices = [Index(value = ["cow_tag"]), Index(value = ["operator_name"])])
data class BirthRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "cow_tag") val cowTag: String,          // tag de la vaca (de female_cows)
    @ColumnInfo(name = "calf_tag") val calfTag: String,         // número de cría
    @ColumnInfo(name = "sex") val sex: String,                  // "M" o "H"
    @ColumnInfo(name = "color") val color: String?,
    @ColumnInfo(name = "weight") val weight: String?,           // texto libre como pediste
    @ColumnInfo(name = "colostrum") val colostrum: Boolean,     // true/false
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "operator_name") val operatorName: String,
    @ColumnInfo(name = "created_at") val createdAt: Long        // millis
)