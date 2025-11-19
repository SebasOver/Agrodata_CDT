package com.example.softwareganadero.domain.potrerosDomain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.potrerosData.HeatDetection

class HeatDetectionRepository(private val db: AgroDatabase) {
    private val dao = db.heatDetectionDao()
    private val cows = db.femaleCowDao()

    suspend fun listCows(): List<String> = cows.listActive().map { it.tag }

    suspend fun save(inHeat: Boolean, cowTag: String?, notes: String?, ts: Long, tsText: String): Long {
        val cow = cowTag?.trim().orEmpty().ifEmpty { null }
        val n = notes?.trim().orEmpty().ifEmpty { null }
        if (inHeat) require(!cow.isNullOrEmpty()) { "Selecciona la vaca en celo" } else require(n != null) { "Ingresa observaciones" }
        val entity = HeatDetection(
            inHeat = inHeat,
            cowTag = cow,
            notes = n,
            createdAt = ts,
            createdAtText = tsText
        )
        return dao.insert(entity)
    }
}