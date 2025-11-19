package com.example.softwareganadero.domain.potrerosDomain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.potrerosData.BirthRecord

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
        createdAtText: String,
        createdAtMillis: Long
    ) {
        require(cowTag.isNotBlank()) { "Vaca requerida" }
        require(calfTag.isNotBlank() && calfTag.toLongOrNull() != null) { "Cría numérica requerida" }
        require(sex == "M" || sex == "H") { "Sexo inválido" }
        val colorOk = color?.trim().orEmpty()
        require(colorOk.isNotEmpty()) { "Color requerido" }
        require(colorOk.none { it.isDigit() }) { "Color solo letras" }
        val weightOk = weight?.trim().orEmpty()
        require(weightOk.isNotEmpty() && weightOk.toDoubleOrNull() != null) { "Peso numérico requerido" }

        val rec = BirthRecord(
            cowTag = cowTag.trim(),
            calfTag = calfTag.trim(),
            sex = sex,
            color = colorOk,
            weight = weightOk,
            colostrum = colostrum,
            notes = notes?.trim().orEmpty().ifEmpty { null },
            operatorName = operatorName.trim(),
            createdAt = createdAtMillis,
            createdAtText = createdAtText
        )
        db.birthRecordDao().insert(rec)
    }
}