package com.example.softwareganadero.domain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.WaterEvaluation

class WaterEvaluationRepository(private val db: AgroDatabase) {
    private val dao = db.waterEvaluationDao()

    // availability: "Escaso" | "Normal" | "Suficiente"
    suspend fun save(availability: String, temperature: String, ts: Long, tsText: String): Long {
        val a = availability.trim()
        require(a in listOf("Escaso","Normal","Suficiente")) { "Selecciona disponibilidad" }
        val temp = temperature.trim().toDoubleOrNull()
        require(temp != null) { "Temperatura numérica requerida" }
        return dao.insert(
            WaterEvaluation(
                availability = a,
                temperature = temp,
                createdAt = ts,
                createdAtText = tsText
            )
        )
    }

    suspend fun list(): List<WaterEvaluation> = dao.getAll()
}