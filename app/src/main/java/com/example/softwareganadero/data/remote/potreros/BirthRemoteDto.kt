package com.example.softwareganadero.data.remote.potreros

data class BirthRemoteDto(
    val cowTag: String = "",
    val calfTag: String = "",
    val sex: String = "",
    val color: String? = null,
    val weight: String? = null,
    val colostrum: Boolean = false,
    val notes: String? = null,
    val operatorName: String = "",
    val createdAt: Long = 0L,
    val createdAtText: String = "",
    val updatedAtMillis: Long = 0L,
    val deleted: Boolean = false
)