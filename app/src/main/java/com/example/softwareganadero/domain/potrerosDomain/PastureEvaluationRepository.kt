package com.example.softwareganadero.domain.potrerosDomain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.potrerosData.PastureEvaluation

class PastureEvaluationRepository(private val db: AgroDatabase) {
    private val dao = db.pastureEvaluationDao()

    suspend fun save(
        kind: String,
        rotation: String,
        paddock: String,
        height: String,
        color: String,
        ts: Long,
        tsText: String
    ): Long {
        val k = kind.trim()
        require(k == "Entrada" || k == "Salida") { "Tipo inválido" }

        val r = rotation.trim(); require(r.isNotEmpty()) { "Rotación requerida" }
        val p = paddock.trim(); require(p.isNotEmpty()) { "Potrero requerido" }

        val h = height.trim(); require(h.toDoubleOrNull() != null) { "Altura numérica requerida" }
        val c = color.trim(); require(c.isNotEmpty()) { "Selecciona un color" }

        return if (k == "Entrada") {
            dao.insert(
                PastureEvaluation(
                    rotation = r,
                    paddock = p,
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
            require(pending != null && pending.heightExit == null) { "No hay Entrada pendiente" }
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