package com.example.softwareganadero.domain.potrerosDomain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.potrerosData.Supplement

class SupplementsRepository(private val db: AgroDatabase) {
    private val dao = db.supplementDao()

    suspend fun save(
        rotation: String,
        lot: String,               // CAMBIO: String
        animalsCount: Int,
        name: String,
        quantity: Double,
        ts: Long,
        tsText: String
    ): Long {
        val r = rotation.trim(); require(r.isNotEmpty()) { "Rotación requerida" }
        val l = lot.trim(); require(l.isNotEmpty()) { "Lote requerido" }
        require(animalsCount >= 0) { "Número de animales inválido" }
        val n = name.trim(); require(n.isNotEmpty() && n.none { it.isDigit() }) { "Nombre inválido" }
        require(quantity >= 0.0) { "Cantidad inválida" }
        return dao.insert(
            Supplement(
                rotation = r,
                lot = l,
                animalsCount = animalsCount,
                name = n,
                quantity = quantity,
                createdAt = ts,
                createdAtText = tsText
            )
        )
    }
}