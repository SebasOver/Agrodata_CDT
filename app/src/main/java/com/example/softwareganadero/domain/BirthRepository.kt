package com.example.softwareganadero.domain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.BirthRecord

class BirthRepository(private val db: AgroDatabase) {
    suspend fun saveBirth(
        cowTag: String,
        calfTag: String,
        sex: String,     // "M" | "H"
        color: String?,
        weight: String?,
        colostrum: Boolean,
        notes: String?,
        operatorName: String
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
            createdAt = System.currentTimeMillis()
        )
        db.birthRecordDao().insert(rec)
    }
}