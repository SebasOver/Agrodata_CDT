package com.example.softwareganadero.domain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.PastureFenceLog

class PastureFenceRepository(private val db: AgroDatabase) {
    private val dao = db.pastureFenceLogDao()

    suspend fun save(rotacion: String, potrero: String, volteos: String, notes: String?, ts: Long, tsText: String): Long {
        val r = rotacion.trim()
        val p = potrero.trim()
        val v = volteos.trim()
        require(r.isNotEmpty()) { "Rotación requerida" }
        require(p.isNotEmpty()) { "Potrero requerido" }
        require(v.isNotEmpty()) { "Volteos requerido" }
        val n = notes?.trim().orEmpty().ifEmpty { null }
        return dao.insert(
            PastureFenceLog(
                rotacion = r,
                potrero = p,
                volteos = v,
                notes = n,
                createdAt = ts,
                createdAtText = tsText
            )
        )
    }

    suspend fun list(): List<PastureFenceLog> = dao.getAll()
}