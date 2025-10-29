package com.example.softwareganadero.data

import androidx.room.*

@Entity(tableName = "producers")
data class Producer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)