package com.example.softwareganadero.domain.corralesDomains

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.corralesData.TriageRecord

class TriageRepository(private val db: AgroDatabase) {
    private val dao = db.triageDao()
    private val options = setOf("Normal","Leve","Moderada","Severa")

    suspend fun save(
        animalNumber: String,
        temperature: Double,
        locomotion: String,
        mucosaColor: String,
        observations: String?,
        ts: Long,
        tsText: String
    ): Long {
        require(animalNumber.isNotBlank() && animalNumber.all { it.isDigit() }) { "Número animal inválido" }
        require(locomotion in options) { "Locomoción inválida" }
        val letters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")
        require(mucosaColor.isNotBlank() && mucosaColor.matches(letters)) { "Color de mucosas inválido" }
        return dao.insert(
            TriageRecord(
                animalNumber = animalNumber.trim(),
                temperature = temperature,
                locomotion = locomotion,
                mucosaColor = mucosaColor.trim(),
                observations = observations,
                createdAt = ts,
                createdAtText = tsText
            )
        )
    }
}
