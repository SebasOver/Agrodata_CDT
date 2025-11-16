package com.example.softwareganadero.domain.visitasDomains

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.visitasData.InstitutionRecord

class InstitutionRepository(private val db: AgroDatabase) {
    private val dao = db.institutionDao()
    private val letters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")

    suspend fun saveEntry(
        visitorName: String,
        reason: String,
        notes: String?,
        ts: Long,
        tsText: String
    ): Long {
        require(visitorName.isNotBlank() && visitorName.matches(letters)) {
            "Nombre de visitante inválido"
        }
        require(reason.isNotBlank() && reason.matches(letters)) {
            "Motivo de visita inválido"
        }
        return dao.insert(
            InstitutionRecord(
                visitorName = visitorName.trim(),
                reason = reason.trim(),
                notes = notes,
                createdAt = ts,
                createdAtText = tsText,
                closedAt = null,
                closedAtText = null
            )
        )
    }

    suspend fun closeVisit(id: Long, ts: Long, tsText: String) {
        val updated = dao.closeVisit(id, ts, tsText)
        require(updated > 0) { "No se encontró la visita para cerrar" }
    }

    suspend fun getOpenVisits(): List<InstitutionRecord> = dao.getOpen()
}