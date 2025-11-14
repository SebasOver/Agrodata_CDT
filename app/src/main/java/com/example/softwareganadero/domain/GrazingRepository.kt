package com.example.softwareganadero.domain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.Grazing

class GrazingRepository(private val db: AgroDatabase) {
    private val dao = db.grazingDao()

    suspend fun save(rotacion: String, potrero: String, animals: Int, createdAt: Long, createdAtText: String): Long {
        val r = rotacion.trim(); val p = potrero.trim()
        require(r.isNotEmpty() && p.isNotEmpty()) { "Rotacion/Potrero vacíos" }
        require(animals > 0) { "Número de animales debe ser mayor que 0" }
        return dao.insert(
            Grazing(
                rotacion = r,
                potrero = p,
                animalsCount = animals,
                createdAt = createdAt,
                createdAtText = createdAtText
            )
        )
    }

    suspend fun list(): List<Grazing> = dao.getAll()
}