package com.example.softwareganadero.domain.cultivosDomains

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.cultivosData.CropRecord

class CropRepository(private val db: AgroDatabase) {
    private val dao = db.cropDao()

    private val letters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")
    // letras + números (alfa-numérico simple)
    private val lettersAndDigits = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ0-9 ]+$")

    suspend fun save(
        lot: String,
        species: String,
        hasPests: Boolean,
        hasDiseases: Boolean,
        notes: String?,
        ts: Long,
        tsText: String
    ): Long {
        require(lot.isNotBlank() && lot.matches(lettersAndDigits)) {
            "Lote inválido (solo letras, números y espacios)"
        }
        require(species.isNotBlank() && species.matches(letters)) {
            "Especie inválida (solo letras y espacios)"
        }
        return dao.insert(
            CropRecord(
                lot = lot.trim(),
                species = species.trim(),
                hasPests = hasPests,
                hasDiseases = hasDiseases,
                notes = notes,
                createdAt = ts,
                createdAtText = tsText
            )
        )
    }
}