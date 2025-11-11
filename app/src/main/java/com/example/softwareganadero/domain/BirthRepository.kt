package com.example.softwareganadero.domain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.BirthRecord

class BirthRepository(private val db: AgroDatabase) {
    suspend fun saveBirth(
        cowTag: String,
        calfTag: String,
        sex: String,
        color: String?,
        weight: String?,
        colostrum: Boolean,
        notes: String?,
        operatorName: String,
        createdAtText: String,       // string formateado a guardar
        createdAtMillis: Long        // opcional, para ordenamiento
    ) {
        val rec = BirthRecord(
            cowTag = cowTag,
            calfTag = calfTag,
            sex = sex,
            color = color?.trim().orEmpty().ifEmpty { null },
            weight = weight?.trim().orEmpty().ifEmpty { null },
            colostrum = colostrum,
            notes = notes?.trim().orEmpty().ifEmpty { null },
            operatorName = operatorName,
            createdAt = createdAtMillis,      // si mantienes el Long
            createdAtText = createdAtText     // nuevo campo legible
        )
        db.birthRecordDao().insert(rec)
    }
}