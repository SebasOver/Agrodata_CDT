package com.example.softwareganadero.domain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.PastureEvaluation

class PastureEvaluationRepository(private val db: AgroDatabase) {
    private val dao = db.pastureEvaluationDao()

    // kind: "Entrada" o "Salida"; height es el string del campo; color nullable
    suspend fun save(kind: String, height: String, color: String?, ts: Long, tsText: String): Long {
        val h = height.trim()
        require(h.isNotEmpty()) { "Altura requerida" }
        val k = kind.trim()
        require(k == "Entrada" || k == "Salida") { "Tipo inválido" }

        if (k == "Salida") {
            val last = dao.last()
            val hasEntryBefore = (last?.heightEntry?.isNotEmpty() == true) || (last?.heightExit == null)
            // Política: permitir salida solo si hay alguna entrada registrada anteriormente.
            require(hasEntryBefore) { "Para registrar Salida debe existir una Entrada previa" }
            return dao.insert(
                PastureEvaluation(
                    heightEntry = null,
                    heightExit = h,
                    color = color?.trim().orEmpty().ifEmpty { null },
                    createdAt = ts,
                    createdAtText = tsText
                )
            )
        } else {
            return dao.insert(
                PastureEvaluation(
                    heightEntry = h,
                    heightExit = null,
                    color = color?.trim().orEmpty().ifEmpty { null },
                    createdAt = ts,
                    createdAtText = tsText
                )
            )
        }
    }

    suspend fun list(): List<PastureEvaluation> = dao.getAll()
}