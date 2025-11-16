package com.example.softwareganadero.domain.corralesDomains

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.corralesData.Palpation

class PalpationRepository(private val db: AgroDatabase) {
    private val dao = db.palpationDao()

    suspend fun save(
        animalNumber: String,
        pregnancyDays: Int,
        observations: String?,
        ts: Long,
        tsText: String
    ): Long {
        require(animalNumber.isNotBlank() && animalNumber.all { it.isDigit() }) { "Número animal inválido" }
        require(pregnancyDays >= 0) { "Días de preñez inválidos" }
        return dao.insert(
            Palpation(
                animalNumber = animalNumber.trim(),
                pregnancyDays = pregnancyDays,
                observations = observations,
                createdAt = ts,
                createdAtText = tsText
            )
        )
    }
}