package com.example.softwareganadero.domain.potrerosDomain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.potrerosData.PastureInventory
import com.example.softwareganadero.data.potrerosData.Precipitation

class PrecipitacionRepository(private val db: AgroDatabase) {
    suspend fun savePrecipitation(amountMm: Double, operator: String, atText: String, atMillis: Long) {
        db.precipitationDao().insert(
            Precipitation(
                amountMm = amountMm,
                operatorName = operator,
                createdAt = atMillis,
                createdAtText = atText
            )
        )
    }

    suspend fun savePastureInventory(lot: Int, healthy: Int, sick: Int, total: Int, operator: String, atText: String, atMillis: Long) {
        require(lot >= 0) { "Lote inválido" }
        require(healthy >= 0 && sick >= 0) { "Valores inválidos" }
        db.pastureInventoryDao().insert(
            PastureInventory(
                lot = lot,
                healthy = healthy,
                sick = sick,
                total = total,
                operatorName = operator,
                createdAt = atMillis,
                createdAtText = atText
            )
        )
    }
}