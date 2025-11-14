package com.example.softwareganadero.domain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.FenceState

class FenceStateRepository(private val db: AgroDatabase) {
    private val fenceDao = db.fenceStateDao()

    suspend fun save(volteos: String, createdAt: Long, createdAtText: String): Long {
        val v = volteos.trim()
        require(v.isNotEmpty()) { "Volteos vacío" }
        return fenceDao.insert(FenceState(volteos = v, createdAt = createdAt, createdAtText = createdAtText))
    }

    suspend fun list(): List<FenceState> = fenceDao.getAll()
}