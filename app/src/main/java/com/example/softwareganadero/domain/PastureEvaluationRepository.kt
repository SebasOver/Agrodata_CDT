package com.example.softwareganadero.domain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.PastureEvaluation

class PastureEvaluationRepository(private val db: AgroDatabase) {
    private val dao = db.pastureEvaluationDao()

    // kind: "Entrada" o "Salida"; height y color obligatorios en ambos
    suspend fun save(kind: String, height: String, color: String, ts: Long, tsText: String): Long {
        val h = height.trim()
        val hNum = h.toDoubleOrNull()
        require(hNum != null) { "Altura numérica requerida" }

        val k = kind.trim()
        require(k == "Entrada" || k == "Salida") { "Tipo inválido" }

        val c = color.trim()
        require(c.isNotEmpty()) { "Selecciona un color" }

        return if (k == "Entrada") {
            dao.insert(
                PastureEvaluation(
                    heightEntry = h,
                    heightExit = null,
                    colorEntry = c,
                    colorExit = null,
                    createdAt = ts,
                    createdAtText = tsText
                )
            )
        } else {
            val pending = dao.lastPendingExit()
            require(pending != null && pending.heightEntry?.isNotEmpty() == true && pending.heightExit == null) {
                "Debe existir una Entrada pendiente para registrar la Salida"
            }
            val updated = dao.updateExit(
                id = pending.id,
                heightExit = h,
                colorExit = c,
                updatedAt = ts,
                updatedAtText = tsText
            )
            require(updated == 1) { "No se pudo actualizar la Salida" }
            pending.id
        }
    }
}