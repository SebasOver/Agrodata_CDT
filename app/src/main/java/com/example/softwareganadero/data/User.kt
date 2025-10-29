package com.example.softwareganadero.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(
    tableName = "users",
    indices = [Index(value = ["full_name"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "role") val role: UserRole = UserRole.OPERATOR,
    @ColumnInfo(name = "active") val active: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole { OPERATOR, ADMIN }

class UserRoleConverter {
    @TypeConverter
    fun fromRole(role: UserRole): String = role.name
    @TypeConverter fun toRole(raw: String): UserRole = UserRole.valueOf(raw)
}