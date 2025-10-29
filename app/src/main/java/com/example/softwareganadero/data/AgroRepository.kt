package com.example.softwareganadero.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AgroRepository(
    private val db: AgroDatabase,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    private val producers = db.producerDao()

    suspend fun saveProducer(name: String): Long = withContext(io) {
        producers.upsert(Producer(name = name.trim()))
    }

    suspend fun listProducers(): List<Producer> = withContext(io) {
        producers.getAll()
    }
}